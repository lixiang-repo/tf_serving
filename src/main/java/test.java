import com.google.protobuf.ByteString;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.StringHelper;
import utils.TFServingFeature;
import utils.TFServingUtils;
import utils.VarType;

import java.util.*;

public class test {
    final Logger logger = LoggerFactory.getLogger(getClass());


    public static Map<String, TFServingFeature> getCtxFeature() {
        Map<String, TFServingFeature> result = new HashMap<>();
        result.put("u_uuid", new TFServingFeature("Meb819348aa6662220c18eb3fcf675940", VarType.STR));
        result.put("label1", new TFServingFeature(0.0f, VarType.FLOAT));
        result.put("label2", new TFServingFeature(0.0f, VarType.FLOAT));
        result.put("label3", new TFServingFeature(0.0f, VarType.FLOAT));

        result.put("hour_str", new TFServingFeature("00", VarType.STR));
        result.put("ui_etype", new TFServingFeature("00", VarType.STR));
        String u_v_ans_uid = "-99";
        String u_v_deep_uid = "-99";
        result.put("u_v_ans_uid", new TFServingFeature(StringHelper.getTfList(u_v_ans_uid, ",", 50, "-99"), VarType.LIST_STR));
        result.put("u_v_deep_uid", new TFServingFeature(StringHelper.getTfList(u_v_deep_uid, ",", 50, "-99"), VarType.LIST_STR));

        return result;
    }

    public static Map<String, TFServingFeature> getSeqFeature() {
        Map<String, TFServingFeature> result = new HashMap<>();
        List<Float> clickCnts = new ArrayList<>();
        for (int i = 0; i < 1025; i++){
            clickCnts.add(1234f);
            clickCnts.add(1235f);
            clickCnts.add(1236f);
        }
        result.put("i_ans_rate", new TFServingFeature(clickCnts, VarType.LIST_FLOAT));

        List<Float> ui_hss = new ArrayList<>();
        for (int i = 0; i < 1025; i++){
            ui_hss.add(1234f);
            ui_hss.add(1235f);
            ui_hss.add(1236f);
        }
        result.put("ui_hs", new TFServingFeature(ui_hss, VarType.LIST_FLOAT));

        List<List<String>> i_v_ans_uids = new ArrayList<>();
        List<List<String>> i_v_deep_uids = new ArrayList<>();
        String i_v_ans_uid1 = "asdfasdf,bgidhgfceddeasdfasdfgfgd";
        String i_v_deep_uid1 = "-99";
        for (int i = 0; i < 1025; i++){
            i_v_ans_uids.add(StringHelper.getTfList(i_v_ans_uid1, ",", 50, "-99"));
            i_v_ans_uids.add(StringHelper.getTfList(i_v_ans_uid1, ",", 50, "-99"));
            i_v_ans_uids.add(StringHelper.getTfList(i_v_ans_uid1, ",", 50, "-99"));

            i_v_deep_uids.add(StringHelper.getTfList(i_v_deep_uid1, ",", 50, "-99"));
            i_v_deep_uids.add(StringHelper.getTfList(i_v_deep_uid1, ",", 50, "-99"));
            i_v_deep_uids.add(StringHelper.getTfList(i_v_deep_uid1, ",", 50, "-99"));
        }
        result.put("i_v_ans_uid", new TFServingFeature(i_v_ans_uids, VarType.LIST_LIST_STR));
        result.put("i_v_deep_uid", new TFServingFeature(i_v_deep_uids, VarType.LIST_LIST_STR));

        return result;
    }

    public static List<Float> predict(String tfServingIp, int tfServingPort, String tfModelName, String tfSignature) {
        PredictionServiceGrpc.PredictionServiceBlockingStub stub = TFServingUtils.getPredictionServiceBlockingStub(tfServingIp, tfServingPort);

        Map<String, TFServingFeature> ctxFeatures = getCtxFeature();
        Map<String, TFServingFeature> seqFeatures = getSeqFeature();
        ByteString inputByteStr = TFServingUtils.BuildSeqExampleByteString(ctxFeatures, seqFeatures);

        TensorShapeProto inputTensorShape = TensorShapeProto.newBuilder().addDim(TensorShapeProto.Dim.newBuilder().setSize(1)).build();

        TensorProto inputProto = TensorProto.newBuilder().addStringVal(inputByteStr).setTensorShape(inputTensorShape).setDtype(DataType.DT_STRING).build();

        ByteString recordTypeByteStr = ByteString.copyFromUtf8("SequenceExample");
        TensorProto recordTypeProto = TensorProto.newBuilder().addStringVal(recordTypeByteStr).setDtype(DataType.DT_STRING).build();

        Predict.PredictRequest.Builder predictRequestBuilder = TFServingUtils.getPredictRequestBuilder(tfModelName, tfSignature);
        predictRequestBuilder.putInputs("record_type", recordTypeProto);
        predictRequestBuilder.putInputs("input", inputProto);
        Predict.PredictResponse response = stub.predict(predictRequestBuilder.build());
        List<Float> scores = response.getOutputsOrThrow("output").getFloatValList();
        return scores;

    }


    public static void main(String args[]) {
        String tfServingIp = "localhost";
        int tfServingPort = 8500;
        String tfModelName = "model";
        String tfSignature = "pred";

        List<Float> scores = predict(tfServingIp, tfServingPort, tfModelName, tfSignature);

        System.out.println(scores);


    }

}
