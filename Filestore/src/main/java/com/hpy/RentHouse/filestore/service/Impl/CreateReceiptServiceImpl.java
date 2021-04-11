package com.hpy.RentHouse.filestore.service.Impl;

import DTO.BillDto;
import DTO.DepositReceiptDto;
import DTO.HouseDto;
import DTO.UserDto;
import Query.DepositQuery;
import Query.ReceiptQuery;
import com.hpy.RentHouse.filestore.service.CreateReceiptService;
import com.hpy.RentHouse.filestore.service.feign.HouseFeign;
import com.hpy.RentHouse.filestore.service.feign.OrderFeign;
import com.hpy.RentHouse.filestore.service.feign.UserFeign;
import com.hpy.RentHouse.filestore.util.OssUtil;
import com.spire.xls.CellRange;
import com.spire.xls.ExcelVersion;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 8:57
 */
@Service
public class CreateReceiptServiceImpl implements CreateReceiptService {

    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private OrderFeign orderFeign;
    @Autowired
    private HouseFeign houseFeign;
    //合同子目录
    @Value("${file.contractPath}")
    private String contractPath;

    @Value("${file.rootPath}")
    private String rootPath;

    @Value("${aliyun.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(CreateReceiptServiceImpl.class);




    //打印收据
    @Override
    public String printReceipt(String bid,String uid){
        logger.info("--------------------------查询收据信息------------------------");
        BillDto bill = orderFeign.findBill(bid);
        if(StringUtils.isNotEmpty(bill.getReceipt())){
            return url + bill.getReceipt();
        }else {
            logger.info("--------------------------查询租客信息------------------------");
            UserDto userById = userFeign.findUserById(uid);
            String realName = userById.getIdCardInfo().getRealName();
            logger.info("--------------------------处理数据------------------------");
            DecimalFormat df=new DecimalFormat("000000.00");
            String prices = df.format(Double.parseDouble(bill.getPrice()));
            prices = prices.substring(0,prices.indexOf("."))+prices.substring(prices.lastIndexOf(".")+1);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(prices.charAt(i)).append(",");
            }
            String priceAr = sb.toString().substring(0,sb.toString().lastIndexOf(","));
            String[] ss = priceAr.split(",");
            String filename = rootPath+bid+".xlsx";
            ossUtil.downLoadFile(filename,"contract/price.xlsx");
            //加载Excel文档
            logger.info("--------------------------处理文件------------------------");
            Workbook workbook = new Workbook();
            workbook.loadFromFile(filename);
            Worksheet worksheet = workbook.getWorksheets().get(0);
            worksheet.insertArray(ss, 5, 7, false);
            //替换名字
            CellRange name = worksheet.findString("realName",true,true);
            name.setText(realName);
            CellRange priceName = worksheet.findString("price",true,true);
            priceName.setText(bill.getPrice());
            workbook.saveToFile(filename, ExcelVersion.Version2016);
            logger.info("--------------------------上传------------------------");
            ossUtil.uploadLocalFile(contractPath+bid+".xlsx",filename,"xlsx");
            logger.info("--------------------------更新------------------------");
            ReceiptQuery receiptQuery = new ReceiptQuery();
            receiptQuery.setBid(bid);
            receiptQuery.setReceipt(contractPath+bid+".xlsx");
            orderFeign.updateReceipt(receiptQuery);
            logger.info("--------------------------返回------------------------");
            return url + receiptQuery.getReceipt();
        }
    }

    //打印押金单
    @Override
    public String printDeposit(String oid){
        logger.info("--------------------------查询押金信息------------------------");
        DepositReceiptDto depositForFile = orderFeign.findDepositForFile(oid);
        String did = depositForFile.getDid();
        if(StringUtils.isNotEmpty(depositForFile.getReceipt())){
            return url + depositForFile.getReceipt();
        }else {
            logger.info("--------------------------查询房屋信息------------------------");
            HouseDto houseHid = houseFeign.findHouseHid(depositForFile.getHid());
            String house = houseHid.getProvince()+houseHid.getCity()+houseHid.getVillage()+houseHid.getAddress()+houseHid.getBuilding()+"栋"+houseHid.getUnit()+"单元"+houseHid.getHouseNum();
            logger.info("--------------------------下载文件------------------------");
            String filename = rootPath+did+".xlsx";
            ossUtil.downLoadFile(filename,"contract/deposit.xlsx");
            logger.info("--------------------------处理文件------------------------");
            Workbook workbook = new Workbook();
            workbook.loadFromFile(filename);
            Worksheet worksheet = workbook.getWorksheets().get(0);
            //替换文字
            CellRange room = worksheet.findString("room",true,true);
            CellRange oidCell = worksheet.findString("oid",true,true);
            CellRange depositCell = worksheet.findString("deposit",true,true);
            CellRange keyDepositCell = worksheet.findString("keyDeposit",true,true);
            CellRange allCell = worksheet.findString("all",true,true);
            room.setText(house);
            oidCell.setText(did);
            depositCell.setText(depositForFile.getBasicDeposit().toString());
            keyDepositCell.setText(depositForFile.getKeyDeposit().toString());
            allCell.setText(Integer.toString(depositForFile.getBasicDeposit()+depositForFile.getKeyDeposit()));
            workbook.saveToFile(filename, ExcelVersion.Version2016);
            logger.info("--------------------------上传------------------------");
            ossUtil.uploadLocalFile(contractPath+did+".xlsx",filename,"xlsx");
            logger.info("--------------------------更新------------------------");
            DepositQuery depositQuery = new DepositQuery();
            depositQuery.setDid(did);
            depositQuery.setReceipt(contractPath+did+".xlsx");
            orderFeign.updateReceiptDeposit(depositQuery);
            logger.info("--------------------------返回------------------------");
            return url + depositQuery.getReceipt();
        }
    }
}
