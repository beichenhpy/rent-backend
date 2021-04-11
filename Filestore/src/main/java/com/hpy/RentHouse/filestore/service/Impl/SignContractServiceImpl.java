package com.hpy.RentHouse.filestore.service.Impl;

import DO.ContractDo;
import DTO.ContractDto;
import DTO.UserDto;
import DTO.UserOrderDto;
import com.hpy.RentHouse.filestore.dao.FileMapper;
import com.hpy.RentHouse.filestore.service.SignContractService;
import com.hpy.RentHouse.filestore.service.feign.UserFeign;
import com.hpy.RentHouse.filestore.util.OssUtil;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.TextSelection;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.TextRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 8:50
 */
@Service
public class SignContractServiceImpl implements SignContractService {
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private FileMapper fileMapper;
    //合同子目录
    @Value("${file.contractPath}")
    private String contractPath;

    @Value("${file.rootPath}")
    private String rootPath;



    private static final Logger logger = LoggerFactory.getLogger(SignContractServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void renterSignContract(String oid) {
        logger.info("----------------------查询合同---------------------------");
        ContractDo contractDo = fileMapper.findContractByOid(oid);
        logger.info("--------------------------查询电子签名------------------------");
        UserDto userDto = userFeign.findUserById(contractDo.getRenterUid());
        logger.info("--------------------------userDto{}------------------------",userDto);
        UserOrderDto renter = new UserOrderDto();
        BeanUtils.copyProperties(userDto,renter);
        logger.info("--------------------------将租客的电子签名置为空------------------------");
        userFeign.updateECardToNull(userDto.getUid());
        //这里拿到的电子签名时保存在oss的url路径
        String renterECard = renter.getECard();
        //合同文件名
        String contractName = contractDo.getOid() + "contract.docx";
        logger.info("--------------------------下载合同------------------------");
        //合同涉及到并发，放在oss上 本地存放路径  服务器上的文件名
        ossUtil.downLoadFile(rootPath + contractName, "contract/contract.docx");
        logger.info("--------------------------下载电子签名------------------------");
        String renterPath = rootPath + renterECard.substring(renterECard.lastIndexOf("/") + 1);
        logger.info("------------------------renterPath:{},", renterPath);
        //下载电子签名
        ossUtil.downLoadFile(renterPath, renterECard);
        logger.info("--------------------------合同处理开始------------------------");
        Document document = new Document(rootPath + contractName);
        document.replace("renter", contractDo.getRenterName(), false, true);
        document.replace("renterID", contractDo.getRenterIdNum(), false, true);
        document.replace("renterPhone", contractDo.getRenterPhone(), false, true);
        document.replace("startTime", contractDo.getStartTime(), false, true);
        document.replace("endTime", contractDo.getEndTime(), false, true);
        document.replace("price", contractDo.getPrice(), false, true);
        document.replace("deposit", contractDo.getDeposit(), false, true);
        document.replace("createTime", contractDo.getStartTime(), false, true);
        logger.info("--------------------------开始替换图片------------------------");
        DocPicture docImg = new DocPicture(document);
        TextSelection textSelection = document.findString("租客签名", true, true);
        docImg.loadImage(renterPath);
        docImg.setWidth(350f);
        docImg.setHeight(150f);
        TextRange textRange = textSelection.getAsOneRange();
        int index = textRange.getOwnerParagraph().getChildObjects().indexOf(textRange);
        textRange.getOwnerParagraph().getChildObjects().insert(index, docImg);
        textRange.getOwnerParagraph().getChildObjects().remove(textRange);
        logger.info("--------------------------保存文件------------------------");
        document.saveToFile(rootPath + contractName, FileFormat.Docx_2013);
        logger.info("--------------------------合同处理结束------------------------");
        document.close();
    }


    /**
     * 先添加电子签名，然后在提交按钮处执行此业务
     * 房东根据订单编号找到对应的合同信息，然后根据添加的电子签名进行签合同
     *
     * @param oid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void ownerSignContract(String oid) {
        logger.info("--------------------------查询合同信息------------------------");
        ContractDo contractByOid = fileMapper.findContractByOid(oid);
        String contractName = contractByOid.getOid() + "contract.docx";
        String renterFilePath = contractPath +contractByOid.getRenterUid()+"_"+contractName;
        String ownerFilePath = contractPath + contractByOid.getOwnerUid()+"_"+ contractName;
        logger.info("--------------------------查询电子签名------------------------");
        UserDto userDto = userFeign.findUserById(contractByOid.getOwnerUid());
        if (userDto == null){
            throw new RuntimeException();
        }
        logger.info("--------------------------将房东的电子签名置为空------------------------");
        userFeign.updateECardToNull(userDto.getUid());
        //转换
        UserOrderDto owner = new UserOrderDto();
        BeanUtils.copyProperties(userDto,owner);
        String ownerECard = owner.getECard();
        String ownerPath = rootPath + ownerECard.substring(ownerECard.lastIndexOf("/") + 1);
        //下载
        ossUtil.downLoadFile(ownerPath, ownerECard);
        Document document = new Document(rootPath + contractName);
        document.replace("owner", contractByOid.getOwnerName(), false, true);
        document.replace("ownerID", contractByOid.getOwnerIdNum(), false, true);
        document.replace("ownerPhone", contractByOid.getOwnerPhone(), false, true);
        logger.info("--------------------------开始替换图片------------------------");


        DocPicture docImg = new DocPicture(document);
        TextSelection textSelection = document.findString("房东签名", true, true);
        docImg.loadImage(ownerPath);
        docImg.setWidth(350f);
        docImg.setHeight(150f);
        TextRange textRange = textSelection.getAsOneRange();
        int index = textRange.getOwnerParagraph().getChildObjects().indexOf(textRange);
        textRange.getOwnerParagraph().getChildObjects().insert(index, docImg);
        textRange.getOwnerParagraph().getChildObjects().remove(textRange);

        logger.info("--------------------------保存文件------------------------");
        document.saveToFile(rootPath + contractName, FileFormat.Docx_2013);
        document.close();
        logger.info("--------------------------合同处理结束------------------------");
        logger.info("--------------------------上传文件------------------------");
        ossUtil.uploadLocalFile(renterFilePath, rootPath + contractName,"word");
        ossUtil.uploadLocalFile(ownerFilePath, rootPath + contractName,"word");
        Integer delete = fileMapper.deleteContract(oid);
        if(delete == 0){
            throw new RuntimeException();
        }
        logger.info("--------------------------删除临时文件------------------------");
        boolean deleteContract = new File(rootPath + contractName).delete();
        if (!deleteContract) {
            throw new RuntimeException();
        }
    }


    /**
     * 插入合同
     *
     * @param contractDto 合同
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addContract(ContractDto contractDto) {
        ContractDo contractDo = new ContractDo();
        //转换
        BeanUtils.copyProperties(contractDto, contractDo);
        fileMapper.addContract(contractDo);
    }







    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteContract(String oid){
        Integer delete = fileMapper.deleteContract(oid);
        if(delete ==0){
            throw new RuntimeException();
        }
    }
}
