package com.hpy.RentHouse.filestore.service.Impl;

import DTO.ElectWaterUsageDto;
import DTO.ElectWaterUsageYearDto;
import DTO.HouseDto;
import DTO.StatisticsDto;
import com.alibaba.fastjson.JSON;
import com.hpy.RentHouse.filestore.service.CreateStatisticsService;
import com.hpy.RentHouse.filestore.service.feign.HouseFeign;
import com.hpy.RentHouse.filestore.util.OssUtil;
import com.spire.xls.CellRange;
import com.spire.xls.ExcelVersion;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import entity.Constant;
import entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/5/14 13:05
 */
@Service
public class CreateStatisticsServiceImpl implements CreateStatisticsService {
    @Autowired
    private HouseFeign houseFeign;
    @Autowired
    private OssUtil ossUtil;
    //合同子目录
    @Value("${file.contractPath}")
    private String contractPath;

    @Value("${file.rootPath}")
    private String rootPath;

    @Value("${aliyun.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(CreateReceiptServiceImpl.class);
    @Override
    public String printElectWaterStatistics(String hid) {
        logger.info("----------------------------下载文件-----------------------------");
        String filename = rootPath + hid + ".xlsx";
        ossUtil.downLoadFile(filename,"contract/statistics.xlsx");
        logger.info("----------------------------数据处理-----------------------------");
        //远程调用房屋服务查询房屋的信息
        HouseDto houseHid = houseFeign.findHouseHid(hid);
        String house = houseHid.getProvince()+houseHid.getCity()+houseHid.getVillage()+houseHid.getAddress()+houseHid.getBuilding()+"栋"+houseHid.getUnit()+"单元"+houseHid.getHouseNum();
        //远程调用房屋服务查询统计信息
        Message electWatersByHid = houseFeign.findAll(hid);
        List<StatisticsDto> list = JSON.parseArray(JSON.toJSONString(electWatersByHid.getData()), StatisticsDto.class);
        //将电费、水费、电量、水量、价格、创建时间转换为二维数组
        String [][]content =new String[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            StatisticsDto e = (StatisticsDto) list.get(i);
            content[i][0]= Integer.toString(e.getElectCount());
            content[i][1]=e.getElectPrice();
            content[i][2]=Integer.toString(e.getWaterCount());
            content[i][3]=e.getWaterPrice();
            content[i][4]=Integer.toString(e.getPrice());
            content[i][5]=e.getCreateTime();
        }
        logger.info("----------------------------文档处理-----------------------------");
        Workbook workbook = new Workbook();
        //加载Excel文档
        workbook.loadFromFile(filename);
        Worksheet worksheet = workbook.getWorksheets().get(0);
        CellRange house1 = worksheet.findString("house", true, true);
        house1.setText(house);
        worksheet.insertArray(content,3,1);
        workbook.saveToFile(filename, ExcelVersion.Version2016);
        logger.info("----------------------------上传文件-----------------------------");
        ossUtil.uploadLocalFile(contractPath+"statistics_"+hid+".xlsx",filename,"xlsx");
        File file = new File(filename);
        boolean delete = file.delete();
        if(!delete){
            throw new RuntimeException();
        }
        return url + contractPath+"statistics_"+hid+".xlsx";
    }
}
