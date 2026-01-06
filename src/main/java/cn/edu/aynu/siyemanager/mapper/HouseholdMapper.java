package cn.edu.aynu.siyemanager.mapper;

import cn.edu.aynu.siyemanager.dto.HouseholdQueryDTO;
import cn.edu.aynu.siyemanager.entity.Household;
import cn.edu.aynu.siyemanager.vo.HouseholdDetailVO;
import cn.edu.aynu.siyemanager.vo.HouseholdListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface HouseholdMapper {

    /**
     * 查询户籍列表（带分页和条件）
     */
    List<HouseholdListVO> selectHouseholdList(@Param("query") HouseholdQueryDTO query);

    /**
     * 查询户籍总数（用于分页）
     */
    long countHouseholdList(@Param("query") HouseholdQueryDTO query);

    /**
     * 根据ID查询户籍详情（带户主信息）
     */
    HouseholdDetailVO selectDetailById(@Param("id") Long id);

    /**
     * 根据ID查询户籍
     */
    Household selectById(@Param("id") Long id);

    /**
     * 根据户号查询（排除指定ID）
     */
    Household selectByHouseholdNoExcludeId(@Param("householdNo") String householdNo,
                                           @Param("excludeId") Long excludeId);

    /**
     * 新增户籍
     */
    int insert(Household household);

    /**
     * 更新户籍
     */
    int update(Household household);

    /**
     * 删除户籍
     */
    int deleteById(@Param("id") Long id);

    Household selectByHouseholdNo(@Param("householdNo") String householdNo);

    @Select("SELECT COUNT(*) FROM household")
    long countAll();

}