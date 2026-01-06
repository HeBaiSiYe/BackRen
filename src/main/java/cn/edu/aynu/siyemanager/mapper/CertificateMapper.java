package cn.edu.aynu.siyemanager.mapper;

import cn.edu.aynu.siyemanager.dto.CertificateQueryDTO;
import cn.edu.aynu.siyemanager.entity.Certificate;
import cn.edu.aynu.siyemanager.vo.CertificateListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CertificateMapper {

    /**
     * 分页查询证件列表
     */
    List<CertificateListVO> selectCertificateList(@Param("query") CertificateQueryDTO query);

    /**
     * 查询证件总数
     */
    long countCertificateList(@Param("query") CertificateQueryDTO query);

    /**
     * 新增证件
     */
    int insert(Certificate certificate);

    /**
     * 根据ID查询证件
     */
    @Select("SELECT * FROM certificate WHERE id = #{id}")
    Certificate selectById(Long id);

    /**
     * 更新证件状态
     */
    @Update("UPDATE certificate SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM certificate")
    long countAll();

    @Select("SELECT COUNT(*) FROM certificate WHERE status = #{status}")
    long countByStatus(@Param("status") Integer status);
}