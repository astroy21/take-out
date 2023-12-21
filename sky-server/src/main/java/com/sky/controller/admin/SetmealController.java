package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 查询分页
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询套餐: {}",setmealPageQueryDTO);
        return Result.success(setmealService.pageQuery(setmealPageQueryDTO));
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐: {}",setmealDTO);
        setmealService.saveWithDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐: {}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 通过id获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("获取套餐信息: {}",id);
        return Result.success(setmealService.getByIdWithDishes(id));
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐: {}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 起售或停售商品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("起售或停售套餐: status {},id {}",status,id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }
}
