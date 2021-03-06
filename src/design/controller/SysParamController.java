package design.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import design.controllerObject.DeCoding;
import design.controllerObject.SysParam;
import design.dao.DeCodingDao;
import design.dao.SysParamDao;
import design.dto.MyResponse;
import design.portWrite.Com2Writer;
import design.service.PortService;
import design.util.Util;

@RequestMapping(value = "sysParam")
@Controller
public class SysParamController {
	
	@Autowired
	SysParamDao dao;
	
	@Autowired
	DataFrameController dfController;
	
	@Autowired
	DeCodingDao dcDao;
	
	@RequestMapping(value = "/getSysParam.action", method = RequestMethod.GET)
	@ResponseBody
	public MyResponse getSysParam() {
		MyResponse myResponse = new MyResponse();
		SysParam sysParam = new SysParam();
		List<Map<String,Object>> spMaps = dao.getSysParam();
		for (Map<String, Object> map : spMaps) {
			sysParam.deviceCoding =  (String) map.get("deviceCoding");
			sysParam.deviceAddr = (int) map.get("deviceAddr");
			sysParam.stateUpInterval = (int) map.get("stateUpInterval");
			sysParam.startOutTime = (int) map.get("startOutTime");
			sysParam.settingTem = (int) map.get("settingTem");
			sysParam.TemCBias = (int) map.get("TemCBias");
		}
		
		myResponse.data = sysParam;
		return myResponse;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateSysParam.action", method = RequestMethod.POST)
	@ResponseBody
	public MyResponse updateSysParam(@RequestBody Map<String, Object> queryMap) {
		MyResponse response = new MyResponse();
		SysParam sysParam = new SysParam();
		sysParam.sysParamId = 1;
		sysParam.deviceCoding = (String) queryMap.get("deviceCoding");
		sysParam.deviceAddr = (int) queryMap.get("deviceAddr");
		sysParam.stateUpInterval = (int) queryMap.get("stateUpInterval");
		sysParam.startOutTime = (int) queryMap.get("startOutTime");
		sysParam.settingTem = (int) queryMap.get("settingTem");
		sysParam.TemCBias = (int) queryMap.get("TemCBias");
		
		int count = dao.updateSysParam(sysParam);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("count", count);
		response.data = result;
		
		//?????????????????????????????????
		DeCoding deCoding = new DeCoding();
		deCoding.deviceCodingId = 1;
		deCoding.deviceCoding = (String) queryMap.get("deviceCoding");
		dcDao.updateDeviceCoding(deCoding);
		//??????????????????????????????
		deCoding.deviceAddress = sysParam.deviceAddr;
		dcDao.updateDeAddress(deCoding);

		// ???????????????????????????
		String whole;
		// ?????????????????????????????????
		String payLoad;

		// ?????????
		String header = "FFFF";
		// ??????
		String flength = "1C";

		// ??????
		String seq;
		String chseqS = Util.decToHex(PortService.frameSeq);
		PortService.frameSeq = (PortService.frameSeq + 1) % 256;
		char[] chseqC = chseqS.toCharArray();
		while (chseqC.length < 2) {
			chseqS = "0" + chseqS;
			chseqC = chseqS.toCharArray();
		}
		seq = chseqS;

		// ????????????
		String deAddress;
		Map<String, Object> sqlResult = (Map<String, Object>) dfController.getDeAddress().data;
		System.out.println("???????????????    " + (int) sqlResult.get("deviceAddr"));
		String chDeAddrS = Util.decToHex((int) sqlResult.get("deviceAddr"));
		char[] chDeAddrC = chDeAddrS.toCharArray();
		while (chDeAddrC.length < 2) {
			chDeAddrS = "0" + chDeAddrS;
			chDeAddrC = chDeAddrS.toCharArray();
		}
		deAddress = chDeAddrS;

		// ?????????
		String functionNum = "05";

		// ????????????
		String data;
			//????????????
		System.out.println("?????????????????????" + (String) queryMap.get("deviceCoding"));
		String deviceCodingS = Util.longToHex(Long.decode((String) queryMap.get("deviceCoding")));
		System.out.println("?????????????????????????????????" + deviceCodingS);
		char[] deviceCodingC = deviceCodingS.toCharArray();
		while(deviceCodingC.length < 10) {
			deviceCodingS = "0" + deviceCodingS;
			deviceCodingC = deviceCodingS.toCharArray();
		}
			//????????????
		String deviceAddrS = Util.decToHex((int) queryMap.get("deviceAddr"));
		char[] deviceAddrC = deviceAddrS.toCharArray();
		while(deviceAddrC.length < 2) {
			deviceAddrS = "0" + deviceAddrS;
			deviceAddrC = deviceAddrS.toCharArray();
		}
			//??????????????????
		String stateUpIntervalS = Util.decToHex((int) queryMap.get("stateUpInterval"));
		char[] stateUpIntervalC = stateUpIntervalS.toCharArray();
		while(stateUpIntervalC.length < 2) {
			stateUpIntervalS = "0" + stateUpIntervalS;
			stateUpIntervalC = stateUpIntervalS.toCharArray();
		}
		   	//?????????????????????
		String startOutTimeS = Util.decToHex((int) queryMap.get("startOutTime"));
		char[] startOutTimeC = startOutTimeS.toCharArray();
		while(startOutTimeC.length < 2) {
			startOutTimeS = "0" + startOutTimeS;
			startOutTimeC = startOutTimeS.toCharArray();
		}
			//????????????
		String settingTemS = String.valueOf((int) queryMap.get("settingTem"));
		int TemInt = Integer.parseInt(settingTemS);
		if(TemInt < 0) {
			TemInt = TemInt * (-1);
			String BTem = Integer.toBinaryString(TemInt);
			char[] BTemC = BTem.toCharArray();
			while(BTemC.length < 6) {
				BTem = "0" + BTem;
				BTemC = BTem.toCharArray();
			}
			BTem = "1" + BTem + "0";
			settingTemS = Util.bstr2Hstr(BTem);
		}else {
			String BTem = Integer.toBinaryString(TemInt);
			char[] BTemC = BTem.toCharArray();
			while(BTemC.length < 6) {
				BTem = "0" + BTem;
				BTemC = BTem.toCharArray();
			}
			BTem = "0" + BTem + "0";
			settingTemS = Util.bstr2Hstr(BTem);
		}
		char[] settingTemC = settingTemS.toCharArray();
		while(settingTemC.length < 2) {
			settingTemS = "0" + settingTemS;
			settingTemC = settingTemS.toCharArray();
		}
			//??????????????????
		String TemCBiasS = Util.decToHex((int) queryMap.get("TemCBias"));
		char[] TemCBiasC = TemCBiasS.toCharArray();
		while(TemCBiasC.length < 2) {
			TemCBiasS = "0" + TemCBiasS;
			TemCBiasC = TemCBiasS.toCharArray();
		}
		data = deviceCodingS + deviceAddrS + "00" + stateUpIntervalS + startOutTimeS
				+ "0000" + settingTemS + TemCBiasS + "FFFF" + "FFFF" + "00";

		// ????????????
		payLoad = flength + seq + deAddress + functionNum + data;

		// ??????crc?????????
		String crc;
		String crcS = Util.byte2HexStr(Util.CRC16(Util.hexStr2Byte(payLoad)));
		char[] crcC = crcS.toCharArray();
		while(crcC.length < 4) {
			crcS = "0" + crcS;
			crcC = crcS.toCharArray();
		}
		crc = crcS;

		// ????????????
		String finishFlag = "FFF7";
		// ???????????????????????????
		whole = header + payLoad + crc + finishFlag;

		System.out.println("??????????????????" + whole);

		// ???????????????????????????????????????
		new Com2Writer(Util.hexStr2Byte(whole));
		new Com2Writer(Util.hexStr2Byte(whole));
		new Com2Writer(Util.hexStr2Byte(whole));

		return response;
	}

}
