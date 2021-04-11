package com.hpy.distributed.House.service.Impl;

import DO.HouseDo;
import DO.HouseInfoDo;
import DTO.HouseDto;
import DTO.HouseInfoDto;
import com.alibaba.fastjson.JSON;
import com.hpy.distributed.House.dao.HouseInfoMapper;
import com.hpy.distributed.House.dao.HouseMapper;
import com.hpy.distributed.House.dao.HouseStatusMapper;
import com.hpy.distributed.House.service.HouseInfoService;
import com.hpy.distributed.House.service.HouseService;
import com.hpy.distributed.House.service.feign.RedisFeign;
import com.hpy.distributed.House.util.OssUtil;
import entity.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 9:05
 */
@Service
public class HouseInfoServiceImpl implements HouseInfoService {

    @Autowired
    private HouseInfoMapper houseInfoMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private RedisFeign redisFeign;
    @Autowired
    private HouseStatusMapper houseStatusMapper;
    //url
    @Value("${aliyun.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(HouseInfoServiceImpl.class);

    private void updatePath(HouseInfoDto houseInfoDto) {
        //houseInfo不为空
        if (houseInfoDto != null) {
            //判断imagesList是否为空
            if (houseInfoDto.getImages() != null) {
                List<String> images = houseInfoDto.getImages();
                List<String> newImages = new ArrayList<>();
                for (String image : images) {
                    image = url + image;
                    newImages.add(image);
                }
                houseInfoDto.setImages(newImages);
            }
            if (houseInfoDto.getVideo() != null) {
                //video
                houseInfoDto.setVideo(url + houseInfoDto.getVideo());
            }
            if (houseInfoDto.getHouseCard() != null) {
                houseInfoDto.setHouseCard(url + houseInfoDto.getHouseCard());
            }
        }

    }

    /**
     * 更新房屋详细信息 更改为未核验
     *
     * @param houseInfoDto 房屋详细信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseInfo(HouseInfoDto houseInfoDto) {
        HouseInfoDo houseInfoDo = new HouseInfoDo();
        BeanUtils.copyProperties(houseInfoDto, houseInfoDo);
        houseInfoMapper.updateHouseInfo(houseInfoDo);
        //更房屋为未核验
        houseStatusMapper.updateCheckToUnCheck(houseInfoDo.getHid());
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + houseInfoDo.getHid(),
                Constant.HOUSEINFO + houseInfoDo.getHid(),
                Constant.RENT + houseInfoDo.getHid());
    }

    /**
     * 更新房屋的图片路径
     *
     * @param path path
     * @param houseInfoId  houseInfoId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseImage(List<String> path, String houseInfoId) {
        logger.info("----------------------------查询数据库中的图片信息 删除oss上的图片-----------------");
        HouseInfoDo houseInfoDo = houseInfoMapper.findHouseInfoByHouseInfoId(houseInfoId);
        String images = houseInfoDo.getImages();
        if (StringUtils.isNotEmpty(images)) {
            List<String> imageList = JSON.parseArray(images, String.class);
            ossUtil.deleteFiles(imageList);
        }
        String pathJson = JSON.toJSONString(path);
        Integer update = houseInfoMapper.updateHouseInfoImg(pathJson, houseInfoId);
        if (update == 0) {
            throw new RuntimeException();
        }
        logger.info("---------------------------更新为未核验-----------------");
        houseStatusMapper.updateCheckToUnCheck(houseInfoDo.getHid());
        logger.info("----------------------------清除缓存-----------------");
        redisFeign.del(Constant.HOUSE_H + houseInfoDo.getHid(), Constant.HOUSEINFO + houseInfoDo.getHid(), Constant.RENT + houseInfoDo.getHid());
    }


    /**
     * 更新房屋的视频路径 在提交按钮处添加方法，改为未核验
     *
     * @param path 路径
     * @param houseInfoId  houseInfoId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseVideo(String path, String houseInfoId) {
        logger.info("----------------------------删除之前的视频信息-----------------");
        HouseInfoDo houseInfoDo = houseInfoMapper.findHouseInfoByHouseInfoId(houseInfoId);
        String video = houseInfoDo.getVideo();
        if (StringUtils.isNotEmpty(video)) {
            ossUtil.deleteFile(video);
        }
        //
        logger.info("----------------------------更新视频路径-----------------");
        Integer update = houseInfoMapper.updateHouseInfoVideo(path, houseInfoId);
        if (update == 0) {
            throw new RuntimeException();
        }
        logger.info("----------------------------更新为未核验-----------------");
        houseStatusMapper.updateCheckToUnCheck(houseInfoDo.getHid());
        logger.info("----------------------------清除缓存-----------------");
        redisFeign.del(Constant.HOUSE_H + houseInfoDo.getHid(), Constant.HOUSEINFO +  houseInfoDo.getHid(), Constant.RENT +  houseInfoDo.getHid());
    }



    /**
     * 更新房产证图片路径
     *
     * @param houseInfoId  houseInfoId
     * @param path 路径
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseCard(String path, String houseInfoId) {
        //删除之前的房产证信息
        HouseInfoDo houseInfoDo = houseInfoMapper.findHouseInfoByHouseInfoId(houseInfoId);
        String houseCard = houseInfoDo.getHouseCard();
        if (StringUtils.isNotEmpty(houseCard)) {
            ossUtil.deleteFile(houseCard);
        }
        Integer update = houseInfoMapper.updateHouseCard(path, houseInfoId);
        if (update == 0) {
            throw new RuntimeException();
        }
        //更新为未核验
        houseStatusMapper.updateCheckToUnCheck(houseInfoDo.getHid());
        //清除缓存
        redisFeign.del(Constant.HOUSE_H + houseInfoDo.getHid(), Constant.HOUSEINFO + houseInfoDo.getHid());
    }

    /**
     * 查询房屋的详细信息
     *
     * @param hid 房屋编号
     * @return 返回详细信息
     */
    @Override
    public HouseInfoDto findHouseInfo(String hid) {
        HouseInfoDto houseInfoDto = null;
        //查询缓存
        String houseInfoDtoJson = redisFeign.get(Constant.HOUSEINFO + hid);
        if (StringUtils.isNotEmpty(houseInfoDtoJson)) {
            houseInfoDto = JSON.parseObject(houseInfoDtoJson, HouseInfoDto.class);

        } else {
            HouseInfoDo houseInfoDo = houseInfoMapper.findHouseInfo(hid);
            if (houseInfoDo != null) {
                houseInfoDto = new HouseInfoDto();
                BeanUtils.copyProperties(houseInfoDo, houseInfoDto);
                String images = houseInfoDo.getImages();
                List<String> imageDtos = JSON.parseArray(images, String.class);
                houseInfoDto.setImages(imageDtos);
                redisFeign.set(Constant.HOUSEINFO + hid, JSON.toJSONString(houseInfoDto));
            }
        }
        if (houseInfoDto != null) {
            updatePath(houseInfoDto);
        }
        return houseInfoDto;
    }

    /**
     * 根据hid查询房屋所有信息,用于出租信息查询使用
     * 使用redis存储
     *
     * @param hid hid
     * @return 返回房屋所有信息
     */
    @Override
    public HouseDto findHouseByHid(String hid) {
        HouseDto houseDto = new HouseDto();
        String houseJson = redisFeign.get(Constant.HOUSE_H + hid);
        if (StringUtils.isNotEmpty(houseJson)) {
            houseDto = JSON.parseObject(houseJson, HouseDto.class);
        } else {
            HouseDo houseDo = houseMapper.findHouseByHid(hid);
            if (houseDo != null) {
                HouseInfoDto houseInfoDto = null;
                HouseInfoDo houseInfoDo = houseDo.getHouseInfo();
                if (houseInfoDo != null) {
                    houseInfoDto = new HouseInfoDto();
                    BeanUtils.copyProperties(houseInfoDo, houseInfoDto);
                    String images = houseInfoDo.getImages();
                    List<String> imageDtos = JSON.parseArray(images, String.class);
                    houseInfoDto.setImages(imageDtos);
                }
                //将houseInfoDo转换成houseInfoDto
                BeanUtils.copyProperties(houseDo, houseDto);
                houseDto.setHouseInfo(houseInfoDto);
                String houseToJson = JSON.toJSONString(houseDto);
                redisFeign.set(Constant.HOUSE_H + hid, houseToJson);
            }
        }
        if (houseDto != null) {
            updatePath(houseDto.getHouseInfo());
        }
        return houseDto;
    }

    //修改房产证信息
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseCardNum(String houseCardNum, String hid) {
        houseInfoMapper.updateHouseCardNum(houseCardNum, hid);
        redisFeign.del(Constant.HOUSEINFO + hid, Constant.HOUSE_H + hid);
    }

    //修改读数
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHouseRecord(Integer waterRecord, Integer houseRecord, Integer carRecord, String houseInfoId,String hid) {
        houseInfoMapper.updateRecord(waterRecord, houseRecord, carRecord, houseInfoId);
        redisFeign.del(Constant.HOUSEINFO + hid, Constant.HOUSE_H + hid);
    }

}
