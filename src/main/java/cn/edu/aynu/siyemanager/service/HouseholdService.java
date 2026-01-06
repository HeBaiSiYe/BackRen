package cn.edu.aynu.siyemanager.service;

import cn.edu.aynu.siyemanager.dto.HouseholdAddDTO;
import cn.edu.aynu.siyemanager.dto.HouseholdQueryDTO;
import cn.edu.aynu.siyemanager.entity.Household;
import cn.edu.aynu.siyemanager.vo.*;

public interface HouseholdService {

    /**
     * 4.2 新建户籍
     */
    HouseholdAddVO addHousehold(HouseholdAddDTO addDTO);

    /**
     * 4.1 获取户籍列表
     */
    PageResult<HouseholdListVO> getHouseholdList(HouseholdQueryDTO queryDTO);

    /**
     * 根据ID获取户籍详情
     */
    Household getHouseholdById(Long id);

    /**
     * 4.3 查看户籍详情
     */
    HouseholdDetailVO getHouseholdDetail(Long id);

    /**
     * 4.5 户籍注销
     * 删除户籍记录且将所有属于这个户籍的人户籍地，户籍id，关系等相关字段置空
     */
    boolean cancelHousehold(Long id);

    /**
     * 更新户籍
     */
    boolean updateHousehold(Household household);

    /**
     * 删除户籍
     */
    boolean deleteHousehold(Long id);

    /**
     * 根据户号查询户籍
     */
    Household getHouseholdByNo(String householdNo);

    
}