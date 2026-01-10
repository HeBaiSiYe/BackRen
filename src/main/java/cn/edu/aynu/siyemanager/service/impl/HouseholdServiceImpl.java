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

    /**
     * 构建完整地址字符串
     */
    private String buildAddress(String province, String city, String district, String detail) {
        StringBuilder address = new StringBuilder();
        if (province != null && !province.isEmpty()) {
            address.append(province);
        }
        if (city != null && !city.isEmpty()) {
            address.append(city);
        }
        if (district != null && !district.isEmpty()) {
            address.append(district);
        }
        if (detail != null && !detail.isEmpty()) {
            address.append(detail);
        }
        return address.toString();
    }

    /**
     * 判断人员的人口类型（复制PersonServiceImpl中的逻辑）
     */
    private Integer determinePersonType(Long householdId, String registerAddress, String currentAddress) {
        if (householdId == null) {
            return 2; // 流动人口
        }

        if ((registerAddress == null || registerAddress.isEmpty()) &&
                (currentAddress == null || currentAddress.isEmpty())) {
            return 1; // 户籍地人口
        }

        if (registerAddress != null && currentAddress != null) {
            return registerAddress.equals(currentAddress) ? 1 : 2;
        }

        return 1;
    }

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

    @Override
    @Transactional
    public boolean updateHousehold(Household household) {
        Household existing = householdMapper.selectById(household.getId());
        if (existing == null) {
            return false;
        }

        int result = householdMapper.update(household);
        if (result > 0) {
            // 更新该户籍下所有非重点人口的人员类型
            updateHouseholdMembersPersonType(household.getId());
        }
        return result > 0;
    }

    /**
     * 更新户籍下所有非重点人口的人员类型
     */
    private void updateHouseholdMembersPersonType(Long householdId) {
        // 获取该户籍下的所有人员
        List<Person> members = personMapper.selectByHouseholdId(householdId);
        if (members == null || members.isEmpty()) {
            return;
        }

        Household household = householdMapper.selectById(householdId);
        if (household == null) return;

        String householdAddress = buildAddress(household.getProvince(), household.getCity(),
                household.getDistrict(), household.getDetailAddress());

        for (Person person : members) {
            // 只更新非重点人口（personType != 3）
            if (person.getPersonType() != 3) {
                // 构建现住地址
                String currentAddress = buildAddress(
                        person.getCurrentProvince(),
                        person.getCurrentCity(),
                        person.getCurrentDistrict(),
                        person.getCurrentDetail()
                );

                // 判断人口类型
                Integer newPersonType = determinePersonType(householdId, householdAddress, currentAddress);

                // 如果需要更新
                if (!newPersonType.equals(person.getPersonType())) {
                    person.setPersonType(newPersonType);
                    personMapper.update(person);
                }
            }
        }
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