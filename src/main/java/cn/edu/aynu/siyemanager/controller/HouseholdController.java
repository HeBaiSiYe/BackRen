package cn.edu.aynu.siyemanager.controller;

import cn.edu.aynu.siyemanager.dto.HouseholdAddDTO;
import cn.edu.aynu.siyemanager.dto.HouseholdQueryDTO;
import cn.edu.aynu.siyemanager.entity.Household;
import cn.edu.aynu.siyemanager.service.HouseholdService;
import cn.edu.aynu.siyemanager.service.PersonService;
import cn.edu.aynu.siyemanager.vo.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/household")
@CrossOrigin
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    /**
     * 4.1 获取户籍列表
     * GET /household/list
     */
    @GetMapping("/list")
    public Result getHouseholdList(@Validated HouseholdQueryDTO queryDTO) {
        try {
            PageResult<HouseholdListVO> pageResult = householdService.getHouseholdList(queryDTO);

            Map<String, Object> data = new HashMap<>();
            data.put("records", pageResult.getRecords());
            data.put("total", pageResult.getTotal());
            data.put("page", pageResult.getPage());
            data.put("pageSize", pageResult.getPageSize());

            return Result.success("查询成功", data);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 4.2 新建户籍
     * POST /household/add
     */
    @PostMapping("/add")
    public Result addHousehold(@Valid @RequestBody HouseholdAddDTO addDTO) {
        try {
            HouseholdAddVO vo = householdService.addHousehold(addDTO);

            Map<String, Object> data = new HashMap<>();
            data.put("id", vo.getId());
            data.put("householdNo", vo.getHouseholdNo());

            return Result.success("创建成功", data);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 4.3 查看户籍详情
     * GET /household/{id}
     */
    @GetMapping("/{id}")
    public Result getHouseholdDetail(@PathVariable Long id) {
        try {
            HouseholdDetailVO detailVO = householdService.getHouseholdDetail(id);

            if (detailVO == null) {
                return Result.error(404, "户籍信息不存在");
            }

            return Result.success("成功", detailVO);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 更新户籍
     * PUT /household/{id}
     */
    @PutMapping("/{id}")
    public Result updateHousehold(@PathVariable Long id, @RequestBody Household household) {
        try {
            household.setId(id);
            boolean success = householdService.updateHousehold(household);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败，户籍不存在");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除户籍
     * DELETE /household/{id}
     */
    @DeleteMapping("/{id}")
    public Result deleteHousehold(@PathVariable Long id) {
        try {
            boolean success = householdService.deleteHousehold(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，该户籍下存在家庭成员");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 4.5 户籍注销
     * DELETE /household/{id}/cancel
     * 删除户籍记录且将所有属于这个户籍的人户籍地，户籍id，关系等相关字段置空
     */
    @DeleteMapping("/{id}/cancel")
    public Result cancelHousehold(@PathVariable Long id) {
        try {
            boolean success = householdService.cancelHousehold(id);

            if (success) {
                return Result.success("户籍注销成功");
            } else {
                return Result.error(404, "户籍信息不存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注销失败：" + e.getMessage());
        }
    }

    /**
     * 根据户籍号获取户籍详情
     * @param householdNo 户籍号
     * @return 户籍信息
     */
    @GetMapping("/by-no/{householdNo}")
    public Result<Household> getHouseholdByNo(@PathVariable String householdNo) {
        try {
            // 参数校验
            if (!StringUtils.hasText(householdNo)) {
                return Result.error(400, "户籍号不能为空");
            }

            // 调用service方法
            Household household = householdService.getHouseholdByNo(householdNo.trim());

            if (household == null) {
                return Result.error(404, "户籍信息不存在");
            }

            return Result.success(household);
        } catch (Exception e) {
            return Result.error("查询失败，请稍后重试");
        }
    }

    @Autowired
    private PersonService personService;

//    /**
//     * 根据ID获取人员详情
//     */
//    @GetMapping("/{id}")
//    public Result<PersonDetailVO> getPersonById(@PathVariable Long id) {
//        if (id == null || id <= 0) {
//            return Result.error("人员ID不能为空");
//        }
//
//        PersonDetailVO person = personService.getPersonDetail(id);
//        if (person == null) {
//            return Result.error(404, "人员信息不存在");
//        }
//
//        return Result.success(person);
//    }
}