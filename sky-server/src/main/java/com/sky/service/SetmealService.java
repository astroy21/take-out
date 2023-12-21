package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void saveWithDishes(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    SetmealVO getByIdWithDishes(Long id);

    void update(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);
}
