package com.pallycon.wowza.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class StringUtil {

    /**
     * 인트 타입의 상수를 바이트 코드로 변경한다.
     *
     * @param value
     * @param length
     * @return
     */
    public  static byte[] intToByteArray(int value, int length) {
        byte[] byteArray = new byte[length];
        for(int i=1;i<=length;i++){
            if(i != length) {
                byteArray[i - 1] = (byte) (value >> 8 * (length - i));
            }else{
                byteArray[i - 1] = (byte)(value);
            }
        }
//        byteArray[0] = (byte)(value >> 24);
//        byteArray[1] = (byte)(value >> 16);
//        byteArray[2] = (byte)(value >> 8);
//        byteArray[3] = (byte)(value);
        return byteArray;
    }

    /**
     * 한글 인코딩(KSC5601)을 "8859_1" 인코딩으로 전환한다.
     *
     * @param str
     * @return String
     * @throws Exception
     */
    public static String kscToAsc(String str) throws Exception {
        try {
            return str != null ? new String(str.getBytes("KSC5601"), "8859_1")
                    : str;
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e);
        }
    }

    /**
     * 8859_1을 KSC5601로 인코딩한다.
     *
     * @param str
     * @return String
     * @throws Exception
     */
    public static String ascToKsc(String str) throws Exception {
        try {
            return str != null ? new String(str.getBytes("8859_1"), "KSC5601")
                    : str;
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e);
        }
    }

    public static String kscToUtf(String str) throws Exception {
        try {
            return str != null ? new String(str.getBytes("8859_1"), "UTF-8")
                    : str;
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e);
        }
    }
    /**
     * 스트링내의 특정문자를 Swap 시킴. <br>
     * 이를 사용하는 것보다는 정규식을 사용하는 것을 권장함. (String.replaceAll())
     *
     * @param input
     * @param oldStr
     * @param newStr
     * @return String
     */
    public static String replaceAll(String input, String oldStr, String newStr) {
        int startIdx = 0;
        int idx = 0;
        int length = oldStr.length();

        StringBuffer sb = new StringBuffer((int) (input.length() * 1.2));
        while ((idx = input.indexOf(oldStr, startIdx)) >= 0) {
            sb.append(input.substring(startIdx, idx));
            sb.append(newStr);
            startIdx = idx + length;
        }
        sb.append(input.substring(startIdx));
        return sb.toString();
    }

    /**
     * 대상 문자열에서 특정 문자 제거.
     *
     * @param input
     * @param oldStr
     * @return String
     */
    public static String remove(String input, String oldStr) {
        int startIdx = 0;
        int idx = 0;
        int length = oldStr.length();

        StringBuffer sb = new StringBuffer((int) (input.length() * 1.2));
        while ((idx = input.indexOf(oldStr, startIdx)) >= 0) {
            sb.append(input.substring(startIdx, idx));
            startIdx = idx + length;
        }
        sb.append(input.substring(startIdx));
        return sb.toString();
    }

    /**
     * <pre>
     *   String을 원하는 크기(byte 단위)로 줄여 마지막 접미사를 붙여 반환한다.
     *   (접미사도 length에 포함된다.)
     *
     *   MS949 인코딩을 기준으로 byte[]를 얻은 후 이를 byte 단위로 나누면서 2바이트를
     *   차지하는 글자에 대한 조정을 수행한다.
     * </pre>
     *
     * @param content
     *            String 내용
     * @param length
     *            원하는 글자 길이 (Byte 수 기준)
     * @param suffix
     *            잘린 글자 뒤에 붙일 문자
     * @return length보다 길경우 suffix를 붙인 String
     */
    public static String fixLength(String content, int length, String suffix) {
        if (content == null) {
            return "";
        }
        if (content.getBytes().length > length) {
            int slen = 0, blen = 0;
            int realLength = length - suffix.getBytes().length;
            while (blen < realLength) {
                blen++;
                slen++;
                if (content.charAt(slen) > '\u00FF') {
                    blen++; // 2-byte character..
                }
            }
            return content.substring(0, slen) + suffix;
        }
        return content;
    }

    /**
     * <pre>
     *  String을 원하는 길이(byte 단위)로 줄여 마지막 접미사를 붙여 반환한다.
     *
     *  접미사의 길이도 길이에 포함되며, 원하는 크기가 접미사의 길이보다 작으면 IllegalArgumentException을 던진다.
     *
     *  주어진 인코딩 기준으로 byte[]를 얻은 후 이를  byte 단위로 나누며, 나누는 과정에 깨지는 문자는 버린다.
     * </pre>
     *
     * @param content
     *            String 내용
     * @param maxWidth
     *            원하는 글자 길이 (byte 수 기준)\
     * @param enc
     *            인코딩
     * @param suffix
     *            잘린 글자 뒤에 붙일 문자
     * @return length보다 길경우 suffix를 붙인 String
     * @throws Exception
     *
     * @see IllegalArgumentException
     */
    public static String abbreviate(String content, int maxWidth, String enc,
                                    String suffix) throws Exception {
        if (content == null)
            return "";
        if (maxWidth < suffix.length())
            throw new Exception(new IllegalArgumentException());
        int ptr = maxWidth - suffix.length();
        String str = null;
        try{
            byte[] bytes = content.getBytes(enc);
            str = new String(bytes, 0, (bytes.length < ptr)? bytes.length : ptr, enc);
        }catch(UnsupportedEncodingException e){
            throw new Exception(e);
        }
        // 인코딩 차이로 깨져 원문과 달라진 글자를 잘라낸다.
        ptr = ((ptr = str.length() - 4) < 0) ? 0 : ptr; // 끝에서 4글자 전부터 비교 시작
        while (ptr < str.length() && str.charAt(ptr) == content.charAt(ptr))
            ptr++;

        return str.substring(0, ptr) + suffix;
    }

    /**
     * 빈문자열 검사.
     */
    public static boolean isEmpty(String input) {
        return (input == null || input.trim().equals(""));
    }

    /**
     * 입력된 문자열을 화폐 단위로 표시한다.
     * @throws Exception
     */
    public static String toCurrency(String currency) throws Exception {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            return format.format(new Long(currency));
        } catch (IllegalArgumentException e) {
            throw new Exception(e);
        }
    }

    /**
     * 입력된 숫자를 화폐 단위표시
     * @throws Exception
     */
    public static String toCurrency(long currency) throws Exception {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            return format.format(new Long(currency));
        } catch (IllegalArgumentException e) {
            throw new Exception(e);
        }
    }

    /**
     * 화폐 단위 포맷을 숫자로 파싱한다.
     * @throws Exception
     */
    public static String parseCurrency(String myString) throws Exception {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            return format.parse(myString).toString();
        } catch (ParseException e) {
            throw new Exception(e);
        }
    }

    /**
     * Array 또는 List 등의 객체를 Delimiter로 구분된 문자로 반환한다.
     *
     * @param obj
     * @param delimiter
     * @return String
     */
    public static String listToString(Object obj, String delimiter) {
        if (obj == null) {
            return "";
        }
        if (obj.getClass().isArray()) {
            StringBuffer buffer = new StringBuffer(512);
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                if (i != 0) {
                    buffer.append(delimiter);
                }
                buffer.append(Array.get(obj, i));
            }
            return buffer.toString();
        } else if (obj instanceof Collection) {
            return listToString(((Collection<?>) obj).iterator(), delimiter);
        } else if (obj instanceof Enumeration) {
            StringBuffer buffer = new StringBuffer(512);
            boolean started = false;
            Enumeration<?> it = (Enumeration<?>) obj;
            while (it.hasMoreElements()) {
                if (started) {
                    buffer.append(delimiter);
                } else {
                    started = true;
                }
                buffer.append(it.nextElement());
            }
            return buffer.toString();
        } else if (obj instanceof Iterator) {
            StringBuffer buffer = new StringBuffer(512);
            boolean started = false;
            Iterator<?> it = (Iterator<?>) obj;
            while (it.hasNext()) {
                if (started) {
                    buffer.append(delimiter);
                } else {
                    started = true;
                }
                buffer.append(it.next());
            }
            return buffer.toString();
        } else if (obj instanceof Map) {
            return listToString(((Map<?, ?>) obj).values(), delimiter);
        } else {
            return obj.toString();
        }
    }

    /**
     * 널값일 경우 대체 객체를 반환한다.
     *
     * @param source
     * @param alernative
     * @return Object
     */
    public static Object nvl(Object source, Object alernative) {
        if (source == null) {
            return alernative;
        }
        return source;
    }

    public static String nvl(String original, String replacement)
    {
        if (original == null || original.trim().length() == 0)
            return replacement;
        else
            return original;
    }
    public static String jsonNvl(org.json.simple.JSONObject jsonObj, String keyName, String replacement){
        if( jsonObj.containsKey(keyName) ){
            return (String)jsonObj.get(keyName);
        }else{
            return replacement;
        }
    }
    public static String isJsonNvl(org.json.simple.JSONObject jsonObj, String keyName, String replacement) {
        if (jsonObj.containsKey(keyName)) {
            return String.valueOf((boolean) jsonObj.get(keyName));
        } else {
            return replacement;
        }
    }
    public static String longJsonNvl(org.json.simple.JSONObject jsonObj, String keyName, String replacement) {
        if (jsonObj.containsKey(keyName)) {
            return String.valueOf((long) jsonObj.get(keyName));
        } else {
            return replacement;
        }
    }

    /**
     * 소수점이 있는 문자열에 , 처리
     *
     */
    public static String formatNumber(String targetVal, String type) {

        int intVal = 0;
        double dblVal = 0;
        String rtnVal = "0";

        if (targetVal == null || targetVal.trim().length() == 0) {
            return "";
        }

        if (targetVal != null) {

            if (type.equals("INT")) { // 순수정수형

                intVal = new Integer(targetVal).intValue();

                DecimalFormat dfInt = new DecimalFormat("#,##0");

                rtnVal = dfInt.format(intVal);

            }
            if (type.equals("FINT")) { // 더블형에서 정수형

                intVal = Math.round(Float.parseFloat(targetVal));

                DecimalFormat dfInt = new DecimalFormat("#,##0");

                rtnVal = dfInt.format(intVal);

            } else if (type.equals("DBL")) { // 순수 더블형

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat("#,##0.00");

                rtnVal = dfDbl.format(dblVal);

            } else if (type.equals("IDBL")) { // 정수가 OVERFLOW(LONG TYPE 정수)

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat("#,##0");

                rtnVal = dfDbl.format(dblVal);

            } else if (type.equals("DDBL")) { // 더블형이 소수점 4자리인경우

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat("#,##0.0000");

                rtnVal = dfDbl.format(dblVal);
            } else if (type.equals("DDBL1")) { // 더블형이 소수점 1자리인경우

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat("#,##0.0");

                rtnVal = dfDbl.format(dblVal);
            } else if (type.equals("DDBL3")) { // 더블형이 소수점 3자리인경우

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat("##,###,###,##0.000");

                rtnVal = dfDbl.format(dblVal);
            } else if (type.equals("DDBL6")) { // 더블형이 소수점 6자리인경우

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat("##,###,###,##0.000000");

                rtnVal = dfDbl.format(dblVal);
            } else if (type.equals("DDBL7")) { // 더블형이 소수점 7자리인경우

                dblVal = new Double(targetVal).doubleValue();

                DecimalFormat dfDbl = new DecimalFormat(
                        "##,###,###,##0.0000000");

                rtnVal = dfDbl.format(dblVal);
            } else if (type.equals("INT4")) { // 정수지만 앞에 영이 붙는것 예) 0025

                int diff = 4 - targetVal.trim().length();
                for (int i = 0; i < diff; i++) {
                    targetVal = "0" + targetVal;
                }
            }
        }

        return rtnVal;
    }

    /**
     * Absolute Path에서 FileName 만 잘라서 반환한다. <br>
     * Windows 와 Unix 계열에서의 디렉토리 구분자가 다르므로, 동일한 데이터에 대해 다른 결과를 반환하므로, 개발/운용시 주의
     * 하기바람. <br>
     * 구분자는 System.getProperty("file.separator") 연산의 결과를 사용한다.
     *
     */
    public static String getFileName(String fullFileName) {
        try {
            return fullFileName.substring(fullFileName.lastIndexOf(System
                    .getProperty("file.separator")) + 1);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 전달 받은 스트링 객체를 주어진 문자 세트로 디코딩 한다.
     *
     * @param value
     *            디코딩될 값이 있는 스트링 객체.
     * @param charset
     *            디코딩 될 문자 세트.
     * @return 디코딩된 스트링 객체.
     */
    public static String decodeCharset(String value, String charset) {
        try {
            Charset set = Charset.forName(charset);
            return set.decode(ByteBuffer.wrap(value.getBytes())).toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 전달 받은 스트링 객체를 주어진 문자 세트로 인코딩 한다.
     *
     * @param value
     *            인코딩될 값이 있는 스트링 객체.
     * @param charset
     *            인코딩 될 문자 세트.
     * @return 인코딩된 스트링 객체.
     */
    public static String encodeCharset(String value, String charset) {
        try {
            Charset set = Charset.forName(charset);
            ByteBuffer bb = set.encode(value);
            return new String(bb.array(), charset);

        } catch (Exception ex) {
            return null;
        }
    }

    /*
     * 평균 구하는 메소드 정의
     */
    public static double mean(double[] data) {
        double sum=0;
        double mean=0;

        for(int i=0; i < data.length ; i++)
        {
            sum += data[i];
        }
        mean = sum / data.length;
        return mean;
    }
    /*
     * 분산 구하는 메소드 정의
     */
    public static double var(double[] data) {
        double ss=0;        // (데이터-평균)^2 의 합
        double var=0;       // 데이터의 분산
        for(int i=0; i < data.length ; i++)
        {
            ss += (data[i]-mean(data))*(data[i]-mean(data));   // 분산구하는식의 일부
        }
        var = ss / (data.length-1);  // 구해진 ss를 (데이터의 수-1)로 나누어 분산을 구함
        return var;         		 // 분산 반환
    }
    /*
     * 표준편차 반환
     */
    public static double std(double[] data) {
        return Math.sqrt(var(data));
    }
    //유형별 전화번호
    public String stringformat(String value){
        String returnValue = "";

        if(value != null){
            if(value.substring(0, 2).equals("02")){
                if(value.length() == 11){
                    returnValue = value.substring(0,3)+"-"+value.substring(3,7)+"-"+value.substring(7,11);
                }else if(value.length() == 10){
                    returnValue = value.substring(0,2)+"-"+value.substring(2,6)+"-"+value.substring(6,10);
                }else if(value.length() == 9){
                    returnValue = value.substring(0,2)+"-"+value.substring(2,5)+"-"+value.substring(5,9);
                }
            }else{
                if(value.length() == 11){
                    returnValue = value.substring(0,3)+"-"+value.substring(3,7)+"-"+value.substring(7,11);
                }else if(value.length() == 10){
                    returnValue = value.substring(0,3)+"-"+value.substring(3,6)+"-"+value.substring(6,10);
                }else if(value.length() == 9){
                    returnValue = value.substring(0,2)+"-"+value.substring(2,5)+"-"+value.substring(5,9);
                }
            }

        }
        return returnValue;

    }
    //Lpad
    public String stringLpad(String value,int length,String format){
        String data = "";
        if(length != value.length())
        {
            for(int i = 0;i<length;i++)
            {
                if(i < value.length())
                {
                    data +=format;
                }
            }
            data += value;
        }else{
            data = value;
        }
        return data;
    }
    //구분자를 제외한 값 불러오기
    public String splitDel(String value, String remove){
        String retValue = "";
        if(value != null){
            String valSub[] = value.split(remove);
            for(int i = 0; i < valSub.length; i++){
                retValue += valSub[i];
            }
        }
        return retValue;
    }

    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static String urlSplit(String url)
    {
        String addr[] = url.split("/");

        return addr[addr.length - 1];
    }

    public static String replaceLicPurchase(String value) {

        String sReturn = "";
        if ( "0".equals(value)) {
            sReturn ="무제한";
        } else if ( "1".equals(value)) {
            sReturn ="기간제";
        } else if ( "2".equals(value)) {
            sReturn ="횟수제";
        } else {
            sReturn ="-";
        }

        return sReturn;
    }

    public static String replaceLicStatus(String value) {

        String sReturn = "";
        if ( "Y".equals(value)) {
            sReturn ="정상";
        } else if ( "N".equals(value)) {
            sReturn ="실패";
        } else {
            sReturn ="";
        }

        return sReturn;
    }

    public static String replaceLicUse(String value) {

        String sReturn = "";
        if ( "Y".equals(value)) {
            sReturn ="발급";
        } else if ( "N".equals(value) || "1".equals(value) || "2".equals(value) || "3".equals(value) || "4".equals(value) ) {
            sReturn ="폐기";
        } else if ( "10000001".equals(value)) {
            sReturn ="구매정보없음";
        } else if ( "10000003".equals(value)) {
            sReturn ="APK 위변조";
        } else if ( "".equals(value)) {
            sReturn = "";
        }

        return sReturn;
    }

    public static String replaceLicUseDetail(String value) {

        String sReturn = "";
        if ( "Y".equals(value)) {
            sReturn ="발급";
        } else if ( "N".equals(value) ) {
            sReturn ="폐기";
        } else if ( "10000001".equals(value)) {
            sReturn ="구매정보없음";
        } else if ( "10000003".equals(value)) {
            sReturn ="APK 위변조";
        } else if ( "10000003".equals(value)) {
            sReturn ="APK 위변조";
        } else if ( "1".equals(value)) {
            sReturn ="구매 취소에 의한 환불";
        } else if ( "2".equals(value)) {
            sReturn ="라이선스 갱신/정책변경";
        } else if ( "3".equals(value)) {
            sReturn ="APP 서비스 중지";
        } else if ( "4".equals(value)) {
            sReturn ="ARM Server ADMIN 을 통한 중지";
        } else if ( "".equals(value)) {
            sReturn = "";
        }

        return sReturn;
    }

    public static String replaceRegDate(String value) {

        String sReturn = "";

        sReturn = value.substring(0,4)
                + "/" + value.substring(4,6)
                + "/" + value.substring(6,8)
                + " " + value.substring(9,11)
                + ":" + value.substring(11,13)
                + ":" + value.substring(13,15) ;

        return sReturn;
    }

    /**
     * 랜덤한 문자열을 원하는 길이만큼 반환합니다.
     *
     * @param length 문자열 길이
     * @return 랜덤문자열
     * */
    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        String chars[] =     "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,1,2,3,4,5,6,7,8,9,0".split(",");

        for (int i=0 ; i<length ; i++)  {
            buffer.append(chars[random.nextInt(chars.length)]);
        }

        return buffer.toString();
    }


    /*
     * 16진수로 랜덤한 문자열을 원하는 길이만큼 반환합니다.
     *
     * @param length 문자열 길이
     * @return 16진수 랜덤문자열
     * */
    public static String getRandomHexToString(int length){
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        String chars[] = "A,B,C,D,E,F,1,2,3,4,5,6,7,8,9,0".split(",");
        for (int i=0 ; i<length ; i++)  {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }

    /*
     * 지정한 길이만큼 공백을 채운다.
     * */
    public static String blankToString( String orig, int length, String add ){
        String buf = "";

        if( orig == null){
            orig = "";
        }

        int space = length - orig.length();
        //System.out.println("space :::" + space);
        int i = 0;
        for(i=0;i<space;i++){
            buf += add;
        }
        //System.out.println("orig --->" + orig);
        orig = buf + orig;

        //System.out.println("buf ==> " + buf + " / orig --->" + orig);
        return orig;
    }

    // byte[] to hex
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    // hex to byte[]
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }
        byte[] ba = new byte[hex.length() / 2];

        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }


    public static String longToHex(long val, int length){
        String hex = Long.toHexString(val);
        length = length*2;
        int hexLength = hex.length();
        if( hexLength < length){
            int zeroLength = length-hexLength;
            for(int i=0; i<zeroLength; i++){
                hex = "0"+hex;
            }
        }
        return hex;
    }

    /**
     * byte array 를 UUID 로 반환합니다.
     * @param bytes
     * @return
     */
    public static String getGuidFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid.toString();
    }

    /**
     * string 으로 된 uuid(xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)를 byte array 로 변환합니다..
     * @param uuid
     * @return
     */
    public static byte[] guidStringToByteArray(String uuid){
        UUID uu = UUID.fromString(uuid);
        long hi = uu.getMostSignificantBits();
        long lo = uu.getLeastSignificantBits();
        return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
    }

    public static String randomBase64String(int length){
        byte[] block = new byte[length];
        new Random().nextBytes(block);
        return Base64.getEncoder().encodeToString(block);
    }

}