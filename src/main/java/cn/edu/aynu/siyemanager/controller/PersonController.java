package cn.edu.aynu.siyemanager.controller;

import cn.edu.aynu.siyemanager.dto.PersonAddDTO;
import cn.edu.aynu.siyemanager.dto.PersonQueryDTO;
import cn.edu.aynu.siyemanager.dto.PersonSearchDTO;
import cn.edu.aynu.siyemanager.dto.PersonUpdateDTO;
import cn.edu.aynu.siyemanager.entity.Person;
import cn.edu.aynu.siyemanager.service.PersonService;
import cn.edu.aynu.siyemanager.vo.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/person")
@CrossOrigin
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * 查询户主ID接口
     * GET /person/search
     * 根据姓名和身份证号查询人员信息
     */
    @GetMapping("/search")
    public Result searchPerson(@Valid PersonSearchDTO searchDTO) {
        try {
            PersonDetailVO personDetail = personService.searchPerson(searchDTO);

            if (personDetail == null) {
                return Result.error(404, "未找到对应的人员信息");
            }

            return Result.success("成功", personDetail);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 新增人员
     * POST /person
     */
    @PostMapping
    public Result addPerson(@RequestBody Person person) {
        try {
            boolean success = personService.addPerson(person);
            if (success) {
                return Result.success("新增成功");
            } else {
                return Result.error("新增失败，身份证号已存在");
            }
        } catch (Exception e) {
            return Result.error("新增失败：" + e.getMessage());
        }
    }

    /**
     * 更新人员
     * PUT /person/{id}
     */
    @PutMapping("/{id}")
    public Result updatePerson(@PathVariable Long id, @RequestBody Person person) {
        try {
            person.setId(id);
            boolean success = personService.updatePerson(person);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败，人员不存在");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除人员
     * DELETE /person/{id}
     */
    @DeleteMapping("/{id}")
    public Result deletePerson(@PathVariable Long id) {
        try {
            boolean success = personService.deletePerson(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询人员列表
     * GET /person/list
     */
    @GetMapping("/list")
    public Result getPersonList(@Validated PersonQueryDTO queryDTO) {
        try {
            PageResult<PersonListVO> pageResult = personService.getPersonList(queryDTO);

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
     * 5.2 新增人员
     * POST /person/add
     */
    @PostMapping("/add")
    public Result addPerson(@Valid @RequestBody PersonAddDTO addDTO) {
        try {
            PersonAddVO vo = personService.addPerson(addDTO);

            Map<String, Object> data = new HashMap<>();
            data.put("id", vo.getId());
            data.put("name", vo.getName());
            data.put("idCard", vo.getIdCard());

            return Result.success("新增成功", data);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取人员详情
     */
    @GetMapping("/{id}")
    public Result<PersonDetailVO> getPersonById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error("人员ID不能为空");
        }

        PersonDetailVO person = personService.getPersonDetail(id);
        if (person == null) {
            return Result.error(404, "人员信息不存在");
        }

        return Result.success(person);
    }

    /**
     * 人员精确搜索（按证件类型）
     * type=1: 查询没有身份证的人
     * type=2: 查询没有居住证的人
     */
    @GetMapping("/search-exact")
    public Result<PersonExactSearchVO> searchPersonExact(
            @RequestParam String name,
            @RequestParam Integer type) {

        if (!StringUtils.hasText(name)) {
            return Result.error("姓名不能为空");
        }
        if (type == null || (type != 1 && type != 2)) {
            return Result.error("证件类型必须为1(身份证)或2(居住证)");
        }

        try {
            PersonExactSearchVO result = personService.searchPersonExact(name.trim(), type);
            if (result == null) {
                return Result.error(404, "未找到对应人员信息");
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}