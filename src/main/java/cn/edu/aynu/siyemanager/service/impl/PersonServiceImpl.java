package cn.edu.aynu.siyemanager.service.impl;

import cn.edu.aynu.siyemanager.dto.PersonAddDTO;
import cn.edu.aynu.siyemanager.dto.PersonQueryDTO;
import cn.edu.aynu.siyemanager.dto.PersonSearchDTO;
import cn.edu.aynu.siyemanager.entity.Household;
import cn.edu.aynu.siyemanager.entity.Person;
import cn.edu.aynu.siyemanager.mapper.HouseholdMapper;
import cn.edu.aynu.siyemanager.mapper.PersonMapper;
import cn.edu.aynu.siyemanager.service.PersonService;
import cn.edu.aynu.siyemanager.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private HouseholdMapper householdMapper;

    @Override
    public PersonDetailVO searchPerson(PersonSearchDTO searchDTO) {
        // 根据姓名和身份证号查询
        PersonDetailVO personDetail = personMapper.selectByNameAndIdCard(
                searchDTO.getName(),
                searchDTO.getIdCard()
        );

        return personDetail;
    }

    @Override
    public Person getPersonById(Long id) {
        return personMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean addPerson(Person person) {
        // 检查身份证号是否已存在
        Person existing = personMapper.selectByIdCard(person.getIdCard());
        if (existing != null) {
            return false;
        }

        // 设置默认值
        if (person.getPersonStatus() == null) {
            person.setPersonStatus(1); // 默认正常状态
        }
        if (person.getPersonType() == null) {
            person.setPersonType(1); // 默认户籍地人口
        }
        if (person.getCreateTime() == null) {
            person.setCreateTime(LocalDateTime.now());
        }

        int result = personMapper.insert(person);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updatePerson(Person person) {
        Person existing = personMapper.selectById(person.getId());
        if (existing == null) {
            return false;
        }

        int result = personMapper.update(person);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deletePerson(Long id) {
        int result = personMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public PageResult<PersonListVO> getPersonList(PersonQueryDTO queryDTO) {
        // 设置默认值
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }

        // 计算offset
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        int pageSize = queryDTO.getPageSize();

        // 查询数据 - 传递queryDTO和分页参数
        List<PersonListVO> records = personMapper.selectPersonList(queryDTO, offset, pageSize);

        // 查询总数
        long total = personMapper.countPersonList(queryDTO);

        // 构建分页结果
        PageResult<PersonListVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());

        return result;
    }

    @Override
    @Transactional
    public PersonAddVO addPerson(PersonAddDTO addDTO) {

        // 2. 如果指定了户籍，校验户籍是否存在
        Household household = null;
        if (addDTO.getHouseholdId() != null) {
            household = householdMapper.selectById(addDTO.getHouseholdId());
            if (household == null) {
                throw new RuntimeException("所属户籍不存在");
            }

            // 如果指定了户籍但没有指定关系，设置默认关系
            if (addDTO.getRelation() == null) {
                // 检查该户籍是否已有户主
                int memberCount = personMapper.countByHouseholdId(addDTO.getHouseholdId());
                if (memberCount == 0) {
                    addDTO.setRelation("户主");  // 第一个成员设为户主
                } else {
                    addDTO.setRelation("家庭成员");  // 默认关系
                }
            }
        }

        // 3. 创建人员对象
        Person person = new Person();
        BeanUtils.copyProperties(addDTO, person);

        // 4. 处理户籍地址和现住地址逻辑
        handleAddressInfo(person, household, addDTO);

        // 5. 设置其他默认值
        if (person.getPersonStatus() == null) {
            person.setPersonStatus(1); // 默认正常状态
        }
        if (person.getPersonType() == null) {
            person.setPersonType(1); // 默认户籍地人口
        }
        person.setCreateTime(LocalDateTime.now());

        // 6. 插入数据
        int result = personMapper.insert(person);
        if (result <= 0) {
            throw new RuntimeException("新增人员失败");
        }

        // 7. 如果该人员是户主，更新户籍表的head_person_id
        if (addDTO.getHouseholdId() != null && "户主".equals(addDTO.getRelation())) {
            Household updateHousehold = new Household();
            updateHousehold.setId(addDTO.getHouseholdId());
            updateHousehold.setHeadPersonId(person.getId());
            householdMapper.update(updateHousehold);
        }

        // 8. 返回结果
        PersonAddVO vo = new PersonAddVO();
        vo.setId(person.getId());
        vo.setName(person.getName());
        vo.setIdCard(person.getIdCard());

        return vo;
    }

    /**
     * 处理地址信息逻辑
     */
    private void handleAddressInfo(Person person, Household household, PersonAddDTO addDTO) {
        // 情况1：没有所属户籍
        if (household == null) {
            // 不管户籍地省市区，household_id外键等与所属户籍相关的键
            person.setHouseholdId(null);
            person.setRegisterProvince(null);
            person.setRegisterCity(null);
            person.setRegisterDistrict(null);
            person.setRegisterDetail(null);

            // 现住地址使用传入的数据（如果提供了）
            person.setCurrentProvince(addDTO.getCurrentProvince());
            person.setCurrentCity(addDTO.getCurrentCity());
            person.setCurrentDistrict(addDTO.getCurrentDistrict());
            person.setCurrentDetail(addDTO.getCurrentDetail());
        }
        // 情况2：有所属户籍但没有现居住地
        else if (household != null &&
                (addDTO.getCurrentProvince() == null || addDTO.getCurrentProvince().isEmpty())) {
            // 现居住地和户籍地都是householdId绑定的户籍地
            person.setHouseholdId(household.getId());
            person.setRegisterProvince(household.getProvince());
            person.setRegisterCity(household.getCity());
            person.setRegisterDistrict(household.getDistrict());
            person.setRegisterDetail(household.getDetailAddress());

            // 现住地址设为户籍地址
            person.setCurrentProvince(household.getProvince());
            person.setCurrentCity(household.getCity());
            person.setCurrentDistrict(household.getDistrict());
            person.setCurrentDetail(household.getDetailAddress());
        }
        // 情况3：有所属户籍也有现居住地
        else if (household != null && addDTO.getCurrentProvince() != null) {
            // 户籍地是householdId绑定的户籍地
            person.setHouseholdId(household.getId());
            person.setRegisterProvince(household.getProvince());
            person.setRegisterCity(household.getCity());
            person.setRegisterDistrict(household.getDistrict());
            person.setRegisterDetail(household.getDetailAddress());

            // 现居住地就是传来的数据
            person.setCurrentProvince(addDTO.getCurrentProvince());
            person.setCurrentCity(addDTO.getCurrentCity());
            person.setCurrentDistrict(addDTO.getCurrentDistrict());
            person.setCurrentDetail(addDTO.getCurrentDetail());
        }
    }

    @Override
    public PersonDetailVO getPersonDetail(Long id) {
        // 1. 查询人员基本信息
        Person person = personMapper.selectById(id);
        if (person == null) {
            return null;
        }

        // 2. 构建PersonDetailVO
        PersonDetailVO vo = new PersonDetailVO();
        vo.setId(person.getId());
        vo.setName(person.getName());
        vo.setIdCard(person.getIdCard());
        vo.setGender(person.getGender());
        vo.setBirthday(person.getBirthday());
        vo.setPhone(person.getPhone());
        vo.setPersonType(person.getPersonType());
        vo.setHouseholdId(person.getHouseholdId());
        vo.setRelation(person.getRelation());

        // 3. 如果有户籍ID，查询户号
        if (person.getHouseholdId() != null) {
            Household household = householdMapper.selectById(person.getHouseholdId());
            if (household != null) {
                vo.setHouseholdNo(household.getHouseholdNo());
            }
        }

        return vo;
    }

    @Override
    public PersonExactSearchVO searchPersonExact(String name, Integer type) {
        // 1. 参数校验
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (type == null || (type != 1 && type != 2)) {
            throw new IllegalArgumentException("证件类型必须为1(身份证)或2(居住证)");
        }

        // 2. 根据type查询人员
        Person person;
        if (type == 1) {
            // 查询没有身份证的人
            person = personMapper.selectByNameExactWithoutIdCard(name.trim());
        } else {
            // 查询没有居住证的人
            person = personMapper.selectByNameExactWithoutResidence(name.trim());
        }

        if (person == null) {
            return null;
        }

        // 3. 构建返回VO
        PersonExactSearchVO vo = new PersonExactSearchVO();
        BeanUtils.copyProperties(person, vo);

        return vo;
    }
}