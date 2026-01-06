package cn.edu.aynu.siyemanager.mapper;

import cn.edu.aynu.siyemanager.dto.PersonQueryDTO;
import cn.edu.aynu.siyemanager.entity.Person;
import cn.edu.aynu.siyemanager.vo.HouseholdMemberVO;
import cn.edu.aynu.siyemanager.vo.PersonDetailVO;
import cn.edu.aynu.siyemanager.vo.PersonListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PersonMapper {

    /**
     * 分页查询人员列表
     */
    // 添加offset和pageSize参数
    List<PersonListVO> selectPersonList(@Param("query") PersonQueryDTO query,
                                        @Param("offset") Integer offset,
                                        @Param("pageSize") Integer pageSize);

    /**
     * 查询人员总数（用于分页）
     */
    long countPersonList(PersonQueryDTO query);

    /**
     * 根据户ID查询家庭成员列表
     */
    List<PersonListVO> selectPersonByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 根据姓名和身份证号查询人员信息（带户籍信息）
     */
    PersonDetailVO selectByNameAndIdCard(@Param("name") String name,
                                         @Param("idCard") String idCard);

    /**
     * 根据户ID统计家庭成员数量
     */
    @Select("SELECT COUNT(*) FROM person WHERE household_id = #{householdId}")
    int countByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 根据ID查询人员
     */
    @Select("SELECT * FROM person WHERE id = #{id}")
    Person selectById(@Param("id") Long id);

    /**
     * 更新人员的户ID和关系
     */
    @Update("UPDATE person SET household_id = #{householdId}, relation = #{relation} WHERE id = #{personId}")
    int updateHouseholdInfo(@Param("personId") Long personId,
                            @Param("householdId") Long householdId,
                            @Param("relation") String relation);

    /**
     * 根据户ID清空所有成员的户籍信息
     */
    @Update("UPDATE person SET " +
            "household_id = NULL, " +
            "relation = NULL, " +
            "register_province = NULL, " +
            "register_city = NULL, " +
            "register_district = NULL, " +
            "register_detail = NULL ," +
            "person_type = 2 " +
            "WHERE household_id = #{householdId}")
    int clearAllMembersHouseholdInfo(@Param("householdId") Long householdId);

    /**
     * 根据户ID查询家庭成员
     */
    List<HouseholdMemberVO> selectMembersByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 新增人员
     */
    int insert(Person person);

    /**
     * 更新人员
     */
    int update(Person person);

    /**
     * 删除人员
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据身份证号查询人员
     */
    @Select("SELECT * FROM person WHERE id_card = #{idCard}")
    Person selectByIdCard(@Param("idCard") String idCard);

    /**
     * 更新人员的户ID
     */
    @Update("UPDATE person SET household_id = #{householdId} WHERE id = #{personId}")
    int updateHouseholdId(@Param("personId") Long personId, @Param("householdId") Long householdId);

    /**
     * 根据户ID查询户籍下的所有人员
     */
    List<Person> selectByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 根据姓名精确查询人员（没有身份证的）
     */
    @Select("SELECT p.id, p.name, p.gender, p.nation, p.phone, p.person_type, " +
            "p.register_province, p.register_city, p.register_district, p.register_detail " +
            "FROM person p " +
            "WHERE p.name = #{name} AND p.id_card IS NULL")
    Person selectByNameExactWithoutIdCard(@Param("name") String name);

    /**
     * 根据姓名精确查询人员（没有居住证的）
     */
    @Select("SELECT p.id, p.name, p.gender, p.nation, p.phone, p.person_type, " +
            "p.register_province, p.register_city, p.register_district, p.register_detail " +
            "FROM person p " +
            "WHERE p.name = #{name} " +
            "AND NOT EXISTS (SELECT 1 FROM certificate c " +
            "WHERE c.person_id = p.id AND c.type = 2 AND c.status != 5)")
    Person selectByNameExactWithoutResidence(@Param("name") String name);

    /**
     * 清空人员身份证号
     */
    @Update("UPDATE person SET id_card = NULL WHERE id = #{personId}")
    int clearIdCard(@Param("personId") Long personId);

    /**
     * 新增：更新人员身份证号
     */
    @Update("UPDATE person SET id_card = #{idCard} WHERE id = #{personId}")
    int updateIdCard(@Param("personId") Long personId, @Param("idCard") String idCard);

    @Select("SELECT COUNT(*) FROM person")
    long countAll();
}