/*
 * File Name: com.huawei.m2m.nsse.j808.client.SimpleHttpServer.java
 *
 * Copyright Notice:
 *      Copyright  1998-2008, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package org.spring.springboot.listener;


import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.domain.LightingVolEleRecord;
import org.spring.springboot.mapper.LightingVolEleRecordMapper;
import org.spring.springboot.utils.ExceptionUtil;
import org.spring.springboot.utils.PubMethod;
import org.spring.springboot.utils.SpringUtil;
import org.spring.springboot.utils.TableNameUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private LightingVolEleRecordMapper lightingVolEleRecordMapper;

    Logger log = LoggerFactory.getLogger(SimpleHttpServer.class);

    public static final String LIGHTING_VOL_ELE_RECORD_TABLENAMEROOT = "nnlightctl_lighting_vol_ele_record";


    {
        this.lightingVolEleRecordMapper = SpringUtil.getBean(LightingVolEleRecordMapper.class);
    }



    /**
     * Log Tool
     */

    private int port;
    private ServerSocketChannel serverSocketChannel = null;
    private ExecutorService executorService;
    private static final int POOL_MULTIPLE = 4;

    private int msgCnt = 0;

    private static String recvString = null;

    public static String getRecvString() {
        return recvString;
    }

    public SimpleHttpServer(int port) throws IOException {
        this.port = port;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE);
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(this.port));

    }

    public void service() {
        while (true) {
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
                executorService.execute(new Handler(socketChannel));
            } catch (IOException e) {
                System.out.println(ExceptionUtil.getBriefExceptionStackTrace(e));
            }
        }
    }

    public static void startServer(final int port) {
        System.out.println("Enabling the server...， Server port = " + port);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    new SimpleHttpServer(port).service();
                } catch (Exception e) {
                    System.out.println("Failed to enable the server. Server port = " + port);
                    System.out.println(ExceptionUtil.getBriefExceptionStackTrace(e));
                    System.out.println();
                }
            }
        });
        t.start();
    }

    public static String recvBuf;

    public static String getRecvBuf() {
        return recvBuf;
    }

    public static void setRecvBuf(String recvBuf) {
        SimpleHttpServer.recvBuf = recvBuf;
    }

    class Handler implements Runnable {
        private SocketChannel socketChannel;

        public Handler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        public void run() {
            handle(socketChannel);
        }

        private void handle(SocketChannel socketChannel) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(2048);
                socketChannel.read(buffer);
                buffer.flip();
                recvString = new String(buffer.array());

                // String responseHead = recvString.substring(0, recvString.lastIndexOf("\n"));
                String responseJsonBody = "";

                if (recvString != null) {

                    try {
                        responseJsonBody = recvString.substring(recvString.lastIndexOf("\n"));
                    } catch (Exception e) {
                        System.out.println("get responseJsonBody fail.");
                    }
                }


                if (msgCnt == 1) {

                    System.out.print("Received the first message pushed by the platform: ");
                } else if (msgCnt == 2) {

                    System.out.print("Received the second message pushed by the platform: ");
                } else if (msgCnt == 3) {

                    System.out.print("Received the third message pushed by the platform: ");
                } else {
                    System.out.print("Received the " + msgCnt + "th message pushed by the platform: ");
                }
                System.out.println(responseJsonBody);

                msgCnt++;
                System.out.println();

                String tableName = TableNameUtil.getTableNameByDate(LIGHTING_VOL_ELE_RECORD_TABLENAMEROOT, new Date());
                try {
                    if (PubMethod.isEmpty(lightingVolEleRecordMapper.selectTableByName(tableName))) {
                        lightingVolEleRecordMapper.createNewTable(tableName);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }

                lightingVolEleRecordMapper.addLightingVolEleRecord(getInfo(responseJsonBody),tableName);
            } catch (IOException e) {
                System.out.println(ExceptionUtil.getBriefExceptionStackTrace(e));
            } finally {
                try {
                    if (socketChannel != null)
                        socketChannel.close();
                } catch (IOException e) {
                    System.out.println(ExceptionUtil.getBriefExceptionStackTrace(e));
                }
            }
        }
    }

    private  LightingVolEleRecord getInfo(String data) {
        JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(data, JSONObject.class);
        JSONObject map = jsonObject.getJSONObject("service").getJSONObject("data");
        LightingVolEleRecord record = new LightingVolEleRecord();
        record.setGmtCreated(new Date());
        record.setGmtUpdated(new Date());
        record.setRecordDatetime(new Date());
        record.setInElectricity(map.getString("InputCurrent"));
        record.setInVoltage(map.getString("InputVoltage"));
        record.setElecFrequency(map.getString("Freq"));
        record.setInActivePower(map.getString("InputActivePower"));
        record.setInReactivePower(map.getString("InpurReactivePower"));
        record.setInSeenPower(map.getString("InputApparentPower"));
        record.setInActiveEnergy(map.getString("InputActiveEnergy"));
        record.setInReactiveEnergy(map.getString("InputReactiveEnergy"));
        record.setInSeenEnergy(map.getString("InputApparentEnergy"));

        String outputCurrent = map.getString("OutputCurrent");
        BigDecimal outCurrent = new BigDecimal(outputCurrent);
        outCurrent = outCurrent.setScale(2, BigDecimal.ROUND_HALF_UP);
        record.setElectricty(outCurrent);

        String outputVoltage = map.getString("OutputVoltage");
        BigDecimal outVoltage = new BigDecimal(outputVoltage);
        outVoltage = outVoltage.setScale(2, BigDecimal.ROUND_HALF_UP);
        record.setVoltage(outVoltage);

        String signalStrength = map.getString("SignalStrength");
        BigDecimal signal = new BigDecimal(signalStrength);
        signal = signal.setScale(2, BigDecimal.ROUND_HALF_UP);
        record.setSignalIntensity(signal);

//        record.setDeviceId(jsonObject.getString("deviceId"));
        return record;

    }

}
