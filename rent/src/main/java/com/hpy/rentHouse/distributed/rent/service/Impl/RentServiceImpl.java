package com.hpy.rentHouse.distributed.rent.service.Impl;


import DO.OnRentInfoDo;
import DTO.*;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpy.rentHouse.distributed.rent.dao.RentMapper;
import com.hpy.rentHouse.distributed.rent.exception.RentUpdateException;
import com.hpy.rentHouse.distributed.rent.service.Feign.HouseFeign;
import com.hpy.rentHouse.distributed.rent.service.Feign.RedisFeign;
import com.hpy.rentHouse.distributed.rent.service.Feign.UserFeign;
import com.hpy.rentHouse.distributed.rent.service.RentService;
import com.hpy.rentHouse.distributed.rent.utils.OssUtil;
import entity.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author: beichenhpy
 * @Date: 2020/2/27 18:57
 * 出租信息业务实现类
 */
@Service
public class RentServiceImpl implements RentService {
    @Autowired
    private RentMapper rentMapper;
    @Autowired
    private HouseFeign houseFeign;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private OssUtil ossUtil;
    //租房子目录
    @Value("${aliyun.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(RentServiceImpl.class);

    /**
     * 找到所有的出租信息 基本信息 只有OnRentDto
     * 无house和user信息 只有简单的基本信息
     *
     * @return 返回出租信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageInfo<OnRentDto> findAllPushRentInfo(int page, int size,String province,String city) {
        PageHelper.startPage(page, size);
        List<OnRentInfoDo> onRentInfoDos = rentMapper.findAllPushRent(province, city);
        return getOnRentDtoPageInfo(page, size, onRentInfoDos);
    }



    /**
     * 查询用户的所有出租信息 不包含house user
     *
     * @param uid 用户编号
     * @return 集合
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageInfo<OnRentDto> findOnRentInfoByUid(int page,int size,String uid) {
        PageHelper.startPage(page, size);
        List<OnRentInfoDo> onRentInfoDos = rentMapper.findOnRentInfoByUid(uid);
        return getOnRentDtoPageInfo(page, size, onRentInfoDos);
    }

    //将do=>dto
    private PageInfo<OnRentDto> getOnRentDtoPageInfo(int page, int size, List<OnRentInfoDo> onRentInfoDos) {
        PageInfo<OnRentInfoDo> onRentInfoDoPageInfo = new PageInfo<>(onRentInfoDos);
        List<OnRentDto> onRentDtos = new ArrayList<>();
        logger.info("-----------rent,{}",onRentInfoDoPageInfo);
        if (!onRentInfoDos.isEmpty()){
            //转换 do-->dto
            for (OnRentInfoDo onRentInfoDo : onRentInfoDos) {
                OnRentDto onRentDto = new OnRentDto();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String createTime = sdf.format(onRentInfoDo.getCreateTime());
                BeanUtils.copyProperties(onRentInfoDo,onRentDto);
                onRentDto.setCreateTime(createTime);
                onRentDtos.add(onRentDto);
            }
            //添加url
            for (OnRentDto onRentDto : onRentDtos) {
                onRentDto.setImage(url + onRentDto.getImage());
            }
        }
        PageInfo<OnRentDto> onRentDtoPageInfo = new PageInfo<>();
        onRentDtoPageInfo.setList(onRentDtos);
        onRentDtoPageInfo.setPageSize(size);
        onRentDtoPageInfo.setPageNum(page);
        onRentDtoPageInfo.setTotal(onRentInfoDoPageInfo.getTotal());
        return onRentDtoPageInfo;
    }


    /**
     * 查询到出租信息
     * 根据hid查询到对应的出租信息
     *
     * @param hid hid
     * @return 返回出租信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnRentForOrderDto findOnRentByHid(String hid) {
        OnRentForOrderDto onRentForOrderDto = null;
        //从缓存中取到详细信息
        String rentJson = redisFeign.get(Constant.RENT + hid);
        if (StringUtils.isNotEmpty(rentJson)) {
            OnRentInfoDto onRentInfoDto = JSON.parseObject(rentJson, OnRentInfoDto.class);
            //转换成简单dto
            onRentForOrderDto = new OnRentForOrderDto();
            BeanUtils.copyProperties(onRentInfoDto,onRentForOrderDto);
        } else {
            OnRentInfoDo onRentInfoDo = rentMapper.findOnRentInfoByHid(hid);
            if (onRentInfoDo != null){
                onRentForOrderDto = new OnRentForOrderDto();
                BeanUtils.copyProperties(onRentInfoDo,onRentForOrderDto);
            }
        }
        return onRentForOrderDto;
    }
    /**
     * 详细信息  包含house user onRentInfo
     * 通过房屋编号查询房屋信息和出租信息，通过用户编号查询到房东的一些基本信息
     *
     * @param hid 房屋编号
     * @param uid 用户编号
     * @return 返回 信息
     */


    @Override
    public OnRentInfoDto findOnRentInfoAllByHidUid(String hid, String uid) {
        OnRentInfoDto onRentInfoDto = null;
        UserRentDto user;
        String rentJson = redisFeign.get(Constant.RENT + hid);
        if (StringUtils.isNotEmpty(rentJson)) {
            onRentInfoDto = JSON.parseObject(rentJson, OnRentInfoDto.class);
        } else {
            //查询出租信息
            OnRentInfoDo onRentInfoDo = rentMapper.findOnRentInfoByHid(hid);
            if (onRentInfoDo != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                onRentInfoDto = new OnRentInfoDto();
                BeanUtils.copyProperties(onRentInfoDo,onRentInfoDto);
                onRentInfoDto.setCreateTime(sdf.format(onRentInfoDo.getCreateTime()));
            }
            if (onRentInfoDto != null) {
                //远程调用查询房屋信息
                HouseDto houseDto = houseFeign.findHouseHid(hid);
                //远程调用查询用户房东的基本信息
                UserDto userDto = userFeign.findUserById(uid);
                //转换
                if (userDto == null){
                    throw new RuntimeException();
                }else {
                    user = new UserRentDto();
                    BeanUtils.copyProperties(userDto,user);
                    user.setProfilePhoto(url + user.getProfilePhoto());
                    onRentInfoDto.setOwner(user);
                    onRentInfoDto.setHouse(houseDto);
                    //放入缓存
                    redisFeign.set(Constant.RENT + hid, JSON.toJSONString(onRentInfoDto));
                }
            }
        }
        if (onRentInfoDto != null) {
            onRentInfoDto.setImage(url + onRentInfoDto.getImage());
        }
        return onRentInfoDto;

    }

    /**
     * 冻结出租信息
     *
     * @param hid 房间编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void frozenOnRent(String hid) {
        Integer frozen = rentMapper.frozenPushRent(hid);
        if (frozen == 0) {
            throw new RuntimeException();
        }
        //清空缓存
        redisFeign.del(Constant.RENT + hid);
    }

    /**
     * 解冻出租信息
     *
     * @param hid 房间编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unfrozenOnRent(String hid) {
        Integer unfrozen = rentMapper.unfrozenPushRent(hid);
        if (unfrozen == 0) {
            throw new RuntimeException();
        }
        //清空缓存
        redisFeign.del(Constant.RENT + hid);
    }


    /**
     * 修改图片
     *
     * @param image 图片信息
     * @param hid   房屋编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateImage(String image, String hid) {
        //如果存在img/rent下则删除对应oss中的照片
        OnRentInfoDo onRentInfoDo = rentMapper.findOnRentInfoByHid(hid);
        String oldImage = onRentInfoDo.getImage();
        String oldPath = oldImage.substring(oldImage.indexOf("/") + 1, oldImage.lastIndexOf("/"));
        if ("rent".equals(oldPath)) {
            ossUtil.deleteFile(oldImage);
        }
        Integer update = rentMapper.modifyRentImage(hid, image);
        if (update == 0) {
            throw new RuntimeException();
        }
        //清空缓存
        redisFeign.del(Constant.RENT + hid);
    }

    /**
     * 更新省份的名字
     * @param oldName 旧名字
     * @param newName 新名字
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProvinceOrCity(String newName, String oldName,String type) {
       if ("province".equals(type)){
           rentMapper.updateProvince(newName, oldName);
       }else {
           rentMapper.updateCity(newName,oldName);
       }
        //清空缓存
        Set<String> keys = redisFeign.keys(Constant.RENT + "*");
        for (String key : keys) {
            redisFeign.del(key);
        }
    }

    //根据hid查询房屋信息
    @Override
    public OnRentDto findRentByHid(String hid) {
        OnRentInfoDo onRentInfoByHid = rentMapper.findOnRentInfoByHid(hid);
        OnRentDto onRentDto = new OnRentDto();
        BeanUtils.copyProperties(onRentInfoByHid,onRentDto);
        onRentDto.setImage(url+onRentDto.getImage());
        return onRentDto;
    }

    /**
     * 上架出租信息
     * @param hid
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upRent(String hid) {
        Integer up = rentMapper.upOnRent(hid);
        if(up == 0){
            throw new RuntimeException();
        }
        houseFeign.updateOnRent(hid);
        redisFeign.del(Constant.RENT + hid);
    }

    /**
     * 下架出租信息
     * @param hid
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void downRent(String hid) {
        Integer down = rentMapper.downOnRent(hid);
        if(down == 0){
            throw new RuntimeException();
        }
        houseFeign.updateUnOnRent(hid);
        redisFeign.del(Constant.RENT + hid);
    }

    //更新房屋时同步更新出租信息，要求房屋在未发布和未出租状态
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseRent(OnRentDto onRentDto) {
        //先查询有无出租信息，如果有则修改，无则放行
        OnRentInfoDo onRentInfoByHid = rentMapper.findOnRentInfoByHid(onRentDto.getHid());
        if(onRentInfoByHid != null){
            //修改
            OnRentInfoDo onRentInfoDo = new OnRentInfoDo();
            BeanUtils.copyProperties(onRentDto,onRentInfoDo);
            rentMapper.updateHouseRent(onRentInfoDo);
            redisFeign.del(Constant.RENT + onRentDto.getHid());
        }
    }


    /**
     * 添加出租基本信息，图片需要查询房屋信息拿到第一张放入数据库
     * 放入缓存中时 使用OnRent+hid的形式
     *
     * @param onRentDto 出租信息
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOnRent(OnRentDto onRentDto) {
        logger.info("--------------houseDto-server查询房屋的照片和视频--------------");
        HouseDto houseDto = houseFeign.findHouseHid(onRentDto.getHid());
        List<String> imagePaths =houseDto.getHouseInfo().getImages();
        //拿到第一张作为封面
        //取到相对路径
        String image = imagePaths.get(0);
        image = image.substring((image.lastIndexOf("/", (image.lastIndexOf("/", image.lastIndexOf("/") - 1)) - 1)) + 1);
        onRentDto.setImage(image);
        onRentDto.setDetailPosition(houseDto.getVillage()+houseDto.getAddress());
        onRentDto.setProvince(houseDto.getProvince());
        onRentDto.setCity(houseDto.getCity());
        //转换
        OnRentInfoDo onRentInfoDo = new OnRentInfoDo();
        BeanUtils.copyProperties(onRentDto,onRentInfoDo);
        onRentInfoDo.setCreateTime(new Date());
        logger.info("----------------------------------rent:{}",onRentDto);
        rentMapper.pushRent(onRentInfoDo);
        logger.info("--------------远程调用house-server更新房屋状态--------------");
        houseFeign.updateOnRent(onRentDto.getHid());
    }

    /**
     * 删除出租信息 未出租才可以删除
     *
     * @param hid hid
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delOnRent(String hid) {
        OnRentInfoDo onRentInfoDo = rentMapper.findOnRentInfoByHid(hid);
        Integer delRent = rentMapper.delRent(hid);
        if (delRent == 0) {
            throw new RentUpdateException();
        }
        logger.info("--------------删除出租信息照片--------------");
        String image = onRentInfoDo.getImage();
        image = image.substring(image.indexOf("/")+1,image.lastIndexOf("/"));
        if("rent".equals(image)){
            //删除出租图片
            ossUtil.deleteFile(onRentInfoDo.getImage());
        }
        logger.info("--------------houseDto-server更新房屋状态--------------");
        //更新house的isOnRent状态为0
        houseFeign.updateUnOnRent(hid);
        //清空缓存
        redisFeign.del(Constant.RENT + hid);
    }

    /**
     * 删除房屋时远程调用
     * 不用更新房屋是否出租，当房屋被删除时删除此出租信息
     *
     * @param hid 出租信息编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delOnRentNoCheck(String hid) {
        OnRentInfoDo onRentInfoDo = rentMapper.findOnRentInfoByHid(hid);
        if (onRentInfoDo != null) {
            Integer delRent = rentMapper.delRent(hid);
            if (delRent == 0) {
                throw new RentUpdateException();
            }
            String image = onRentInfoDo.getImage();
            image = image.substring(image.indexOf("/")+1,image.lastIndexOf("/"));
            if("rent".equals(image)){
                //删除出租图片
                ossUtil.deleteFile(onRentInfoDo.getImage());
            }
            //清空缓存
            redisFeign.del(Constant.RENT + hid);
        }
    }

    /**
     * 修改发布的出租信息
     * 只能时未出租时才能修改
     *
     * @param onRentDto 出租信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyOnRent(OnRentDto onRentDto) {
        OnRentInfoDo onRentInfoDo = new OnRentInfoDo();
        BeanUtils.copyProperties(onRentDto,onRentInfoDo);
        Integer modifyPushRent = rentMapper.modifyPushRent(onRentInfoDo);
        if (modifyPushRent == 0) {
            throw new RentUpdateException();
        }
        //清空缓存
        redisFeign.del(Constant.RENT + onRentDto.getHid());
    }


}
