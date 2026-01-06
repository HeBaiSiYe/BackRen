package cn.edu.aynu.siyemanager.service.impl;

import cn.edu.aynu.siyemanager.dto.HouseholdAddDTO;
import cn.edu.aynu.siyemanager.dto.HouseholdQueryDTO;
import cn.edu.aynu.siyemanager.entity.Household;
import cn.edu.aynu.siyemanager.entity.Person;
import cn.edu.aynu.siyemanager.mapper.HouseholdMapper;
import cn.edu.aynu.siyemanager.mapper.PersonMapper;
import cn.edu.aynu.siyemanager.service.HouseholdService;
import cn.edu.aynu.siyemanager.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HouseholdServiceImpl implements HouseholdService {

    @Autowired
    private HouseholdMapper householdMapper;

    @Autowired
    private PersonMapper personMapper;

    @Override
    @Transactional
    public HouseholdAddVO addHousehold(HouseholdAddDTO addDTO) {
        // 1. 校验户号是否已存在
        Household existing = householdMapper.selectByHouseholdNo(addDTO.getHouseholdNo());
        if (existing != null) {
            throw new RuntimeException("户号已存在");
        }

        // 2. 如果指定了户主，校验户主是否存在且未被其他户关联
        Person headPerson = null;
        if (addDTO.getHeadPersonId() != null) {
            headPerson = personMapper.selectById(addDTO.getHeadPersonId());
            if (headPerson == null) {
                throw new RuntimeException("户主不存在");
            }

            // 检查该人员是否已经是其他户的成员
            if (headPerson.getHouseholdId() != null) {
                throw new RuntimeException("该人员已是其他户的成员，无法设置为户主");
            }
        }

        // 3. 创建户籍对象
        Household household = new Household();
        BeanUtils.copyProperties(addDTO, household);

        // 设置默认值
        if (household.getStatus() == null) {
            household.setStatus(1); // 默认正常状态
        }
        household.setCreateTime(LocalDateTime.now());

        // 4. 插入户籍数据
        int result = householdMapper.insert(household);
        if (result <= 0) {
            throw new RuntimeException("创建户籍失败");
        }

        // 5. 如果指定了户主，更新户主的household_id、关系和户籍地址
        if (addDTO.getHeadPersonId() != null && household.getId() != null && headPerson != null) {
            // 5.1 创建更新人员的对象
            Person updatePerson = new Person();
            updatePerson.setId(addDTO.getHeadPersonId());
            updatePerson.setHouseholdId(household.getId());
            updatePerson.setRelation("户主");

            // 5.2 更新户籍地址（从户籍信息复制到人员的户籍地址）
            if (addDTO.getProvince() != null) {
                updatePerson.setRegisterProvince(addDTO.getProvince());
                updatePerson.setCurrentProvince(addDTO.getProvince()); // 也可以更新现住地址
            }
            if (addDTO.getCity() != null) {
                updatePerson.setRegisterCity(addDTO.getCity());
                updatePerson.setCurrentCity(addDTO.getCity());
            }
            if (addDTO.getDistrict() != null) {
                updatePerson.setRegisterDistrict(addDTO.getDistrict());
                updatePerson.setCurrentDistrict(addDTO.getDistrict());
            }
            if (addDTO.getDetailAddress() != null) {
                updatePerson.setRegisterDetail(addDTO.getDetailAddress());
                updatePerson.setCurrentDetail(addDTO.getDetailAddress());
            }

            // 5.3 执行更新
            int updateResult = personMapper.update(updatePerson);

            if (updateResult <= 0) {
                throw new RuntimeException("更新户主信息失败");
            }

            // 5.4 验证更新是否成功（可选）
            Person updatedPerson = personMapper.selectById(addDTO.getHeadPersonId());
            if (updatedPerson == null || !household.getId().equals(updatedPerson.getHouseholdId())) {
                throw new RuntimeException("户主信息更新失败");
            }
        }

        // 6. 返回结果
        HouseholdAddVO vo = new HouseholdAddVO();
        vo.setId(household.getId());
        vo.setHouseholdNo(household.getHouseholdNo());

        return vo;
    }

    @Override
    public HouseholdDetailVO getHouseholdDetail(Long id) {
        // 1. 查询户籍基本信息
        HouseholdDetailVO detailVO = householdMapper.selectDetailById(id);
        if (detailVO == null) {
            return null;
        }

        // 2. 查询家庭成员
        List<HouseholdMemberVO> members = personMapper.selectMembersByHouseholdId(id);
        detailVO.setMembers(members);

        return detailVO;
    }

    @Override
    public PageResult<HouseholdListVO> getHouseholdList(HouseholdQueryDTO queryDTO) {
        // 设置默认值
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }

        // 查询数据
        List<HouseholdListVO> records = householdMapper.selectHouseholdList(queryDTO);

        // 查询总数
        long total = householdMapper.countHouseholdList(queryDTO);

        // 构建分页结果
        PageResult<HouseholdListVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());

        return result;
    }

    @Override
    public Household getHouseholdById(Long id) {
        return householdMapper.selectById(id);
    }

    @Transactional
    public boolean updateHousehold(Household household) {
        // 1. 检查户籍是否存在
        Household existing = householdMapper.selectById(household.getId());
        if (existing == null) {
            return false; // 户籍不存在
        }

        // 2. 如果更新了地址信息，需要同步更新户主的户籍地址
        boolean addressChanged = isAddressChanged(existing, household);
        if (addressChanged && existing.getHeadPersonId() != null) {
            // 获取户主信息
            Person headPerson = personMapper.selectById(existing.getHeadPersonId());
            if (headPerson != null) {
                // 创建更新人员的对象
                Person updatePerson = new Person();
                updatePerson.setId(existing.getHeadPersonId());

                // 更新户籍地址
                if (household.getProvince() != null) {
                    updatePerson.setRegisterProvince(household.getProvince());
                    // 如果需要，也可以更新现住地址
                    // updatePerson.setCurrentProvince(household.getProvince());
                }
                if (household.getCity() != null) {
                    updatePerson.setRegisterCity(household.getCity());
                    // updatePerson.setCurrentCity(household.getCity());
                }
                if (household.getDistrict() != null) {
                    updatePerson.setRegisterDistrict(household.getDistrict());
                    // updatePerson.setCurrentDistrict(household.getDistrict());
                }
                if (household.getDetailAddress() != null) {
                    updatePerson.setRegisterDetail(household.getDetailAddress());
                    // updatePerson.setCurrentDetail(household.getDetailAddress());
                }

                // 执行更新
                personMapper.update(updatePerson);
            }
        }

        // 3. 更新户籍数据
        int result = householdMapper.update(household);
        return result > 0;
    }

    /**
     * 检查地址是否发生变化
     */
    private boolean isAddressChanged(Household existing, Household updated) {
        return (updated.getProvince() != null && !updated.getProvince().equals(existing.getProvince())) ||
                (updated.getCity() != null && !updated.getCity().equals(existing.getCity())) ||
                (updated.getDistrict() != null && !updated.getDistrict().equals(existing.getDistrict())) ||
                (updated.getDetailAddress() != null && !updated.getDetailAddress().equals(existing.getDetailAddress()));
    }

    @Override
    @Transactional
    public boolean deleteHousehold(Long id) {
        // 检查是否有家庭成员（不能删除有成员的户籍）
        int memberCount = personMapper.countByHouseholdId(id);
        if (memberCount > 0) {
            return false; // 有家庭成员，不能删除
        }

        // 删除户籍
        int result = householdMapper.deleteById(id);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean cancelHousehold(Long id) {
        try {
            // 1. 检查户籍是否存在
            Household household = householdMapper.selectById(id);
            if (household == null) {
                return false;
            }

            System.out.println("开始注销户籍: " + household.getHouseholdNo() + " (ID: " + id + ")");

            // 2. 先清空所有相关人员的户籍信息（使用专门的方法）
            System.out.println("清空户籍下所有人员的户籍信息...");
            int clearedCount = personMapper.clearAllMembersHouseholdInfo(id);
            System.out.println("已清空 " + clearedCount + " 个人员的户籍信息");

            // 3. 再删除户籍记录
            System.out.println("删除户籍记录...");
            int deleteResult = householdMapper.deleteById(id);

            if (deleteResult > 0) {
                System.out.println("户籍注销成功");
            } else {
                System.out.println("户籍删除失败");
            }

            return deleteResult > 0;

        } catch (Exception e) {
            System.err.println("户籍注销失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("户籍注销失败：" + e.getMessage());
        }
    }

    /**
     * 获取户籍下的所有人员
     */
    private List<Person> getHouseholdMembers(Long householdId) {
        // 使用已有的 selectMembersByHouseholdId 方法，或者创建新的
        // 这里假设已经有这个方法
        return personMapper.selectByHouseholdId(householdId);
    }

    /**
     * 清空人员的户籍相关信息
     */
    private void clearPersonHouseholdInfo(Long personId) {
        try {
            // 创建更新对象并设置要清空的字段
            Person updatePerson = new Person();
            updatePerson.setId(personId);

            // 必须设置这些字段为null，让MyBatis生成SET子句
            updatePerson.setHouseholdId(null);      // 清空户籍ID
            updatePerson.setRelation(null);         // 清空关系

            // 如果需要清空户籍地址，也需要设置
            updatePerson.setRegisterProvince(null);
            updatePerson.setRegisterCity(null);
            updatePerson.setRegisterDistrict(null);
            updatePerson.setRegisterDetail(null);

            System.out.println("准备更新人员ID: " + personId +
                    ", householdId: " + updatePerson.getHouseholdId() +
                    ", relation: " + updatePerson.getRelation());

            // 执行更新
            int result = personMapper.update(updatePerson);
            System.out.println("清空人员" + personId + "的户籍信息，结果：" + result);

        } catch (Exception e) {
            System.err.println("清空人员" + personId + "的户籍信息失败：" + e.getMessage());
            throw e;
        }
    }

    @Override
    public Household getHouseholdByNo(String householdNo) {
        return householdMapper.selectByHouseholdNo(householdNo);
    }

    
}