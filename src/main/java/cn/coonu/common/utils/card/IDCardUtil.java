package cn.coonu.common.utils.card;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 中国大陆居民身份证件相关工具类;
 *
 * @author Jack wang
 */
public class IDCardUtil {

    /*--------------------------------------------
	|             C O N S T A N T S             |
	============================================*/

    private final static Map<Integer, String> LAST_CODE_MAP = new HashMap<>();
    private final static int[]                WEIGHTING_FACTORS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private final static int                  LENGTH_OF_ID = 18;
    /**
     * 这儿用Properties(继承自Hashtable)性能太低,用HashMap好一点
     */
    private final static Properties           PROVINCE_CODE = new Properties();

    static{
        LAST_CODE_MAP.put(0,  "1");
        LAST_CODE_MAP.put(1,  "0");
        LAST_CODE_MAP.put(2,  "X");
        LAST_CODE_MAP.put(3,  "9");
        LAST_CODE_MAP.put(4,  "8");
        LAST_CODE_MAP.put(5,  "7");
        LAST_CODE_MAP.put(6,  "6");
        LAST_CODE_MAP.put(7,  "5");
        LAST_CODE_MAP.put(8,  "4");
        LAST_CODE_MAP.put(9,  "3");
        LAST_CODE_MAP.put(10, "2");

        Reader reader = null;
        try {
            reader = new InputStreamReader(IDCardUtil.class.getResourceAsStream("/properties/province.properties"), "GBK");
            PROVINCE_CODE.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*--------------------------------------------
	|               M E T H O D S               |
	============================================*/

    /**
     * 验证中国大陆居民身份证号码是否符合规则
     * 此方法只验证了号码长度,出生日期是否符合规范,末位校验码是否符合计算结果及前17位是否全是数字;
     * 还需要验证的是区域码(区域码由民政部指定,每年都会变化,所以不好验证)
     *
     * 规则:
     *   总共18位,1~6表示行政区域码,7~14表示出生日期,15~17表示顺序码,奇数男性,偶数女性,最后一位是校验码
     * 验证码计算规则:
     *   前面17位分别和对应权重因数的积之和 mod 11的结果在LAST_CODE_MAP中找对应的结果
     *
     * @param id 中国大陆居民身份证号码
     * @return 是否符合规则
     */
    public static boolean verifyID(String id){

        //校验长度
        if (id.length() != LENGTH_OF_ID){
            return false;
        }

        //校验出生日期是否符合
        try {
            Date birthday = new SimpleDateFormat("yyyyMMdd").parse(id.substring(6, 14));
            if (birthday.after(new Date())){
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

        //校验最后一位校验码
        char[] charArray = id.toCharArray();
        int sum = 0;

        for (int i = 0; i < charArray.length - 1; i++) {
            char c = charArray[i];
            int num;
            try {
                num = Integer.parseInt(String.valueOf(c));
            }catch (NumberFormatException e){
                return false;
            }
            sum += num * WEIGHTING_FACTORS[i];
        }

        return LAST_CODE_MAP.get(sum % 11).equals(String.valueOf(charArray[17]).toUpperCase());
    }

    /**
     * 解析身份证号码包含的信息,省份,生日,年龄,性别;
     *
     * @param id 中国大陆地区身份证号码
     * @return 返回包含信息的对象,解析错误返回null
     */
    public static InformationOfID parseID(String id){

        //校验身份证号码是否符合规则
        if (!verifyID(id)){
            return null;
        }

        String birthdayCode = id.substring(6, 14);
        String genderCode = id.substring(16, 17);
        String provinceCode = id.substring(0, 2);

        //出生日期
        Date birthday;
        try {
            birthday = new SimpleDateFormat("yyyyMMdd").parse(birthdayCode);
        }catch (ParseException e){
            return null;
        }

        //年龄
        Calendar now = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        birth.set(Calendar.YEAR, now.get(Calendar.YEAR));
        if (birth.compareTo(now) > 0){
            age--;
        }

        //性别
        String gender = (Integer.parseInt(genderCode) % 2) == 0 ? "女" : "男";

        //省份
        String province = PROVINCE_CODE.getProperty(provinceCode);

        return new InformationOfID().setBirthday(birthday).setAge(age).setGender(gender).setProvince(province);
    }

}