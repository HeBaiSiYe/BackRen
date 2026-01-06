package cn.edu.aynu.siyemanager.service;

import cn.edu.aynu.siyemanager.dto.PersonAddDTO;
import cn.edu.aynu.siyemanager.dto.PersonQueryDTO;
import cn.edu.aynu.siyemanager.dto.PersonSearchDTO;
import cn.edu.aynu.siyemanager.entity.Person;
import cn.edu.aynu.siyemanager.vo.*;

import java.util.List;

public interface PersonService {

    /**
     * 分页查询人员列表
     */
    PageResult<PersonListVO> getPersonList(PersonQueryDTO queryDTO);

    /**
     * 根据姓名和身份证号查询人员信息
     */
    PersonDetailVO searchPerson(PersonSearchDTO searchDTO);

    /**
     * 根据ID获取人员详情
     */
    Person getPersonById(Long id);

    /**
     * 新增人员
     */
    boolean addPerson(Person person);

    /**
     * 更新人员
     */
    boolean updatePerson(Person person);

    /**
     * 删除人员
     */
    boolean deletePerson(Long id);

    /**
     * 5.2 新增人员
     */
    PersonAddVO addPerson(PersonAddDTO addDTO);

    /**
     * 根据ID获取人员详情
     */
    PersonDetailVO getPersonDetail(Long id);

    /**
     * 人员精确搜索
     * @param name 姓名
     * @param type 1-身份证，2-居住证
     */
    PersonExactSearchVO searchPersonExact(String name, Integer type);
}