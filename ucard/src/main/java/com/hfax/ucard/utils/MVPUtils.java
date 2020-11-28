package com.hfax.ucard.utils;

import android.text.TextUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MVPUtils {
    public enum Method {
        GET, POST, FORM;
        public static Method parse(String method){
            if(!TextUtils.isEmpty(method)){
                for(Method m:Method.values()){
                    if(TextUtils.equals(m.toString(),method.toUpperCase())){
                        return m;
                    }
                }
            }
            return  GET;

        }
    }

    /**
     * 获取传入的对象的具体类型
     *
     * @param obj
     * @return
     */
    public static Type getTType(Object obj) {
        try {
            if(obj!=null){
                Class childClazz = obj.getClass(); //子类字节码对象
                //得到父类的字节码BaseDaoImpl的字节码 ， 这份字节码上带有泛型数据
                /**
                 * 虽然这个方法，返回值说的是Type ，
                 * 但是其实返回的是ParameterizedType的实现类类型。
                 * 所以我们使用ParameterizedTypeImpl接口来接收。
                 */
                ParameterizedType genericSuperclass = (ParameterizedType) childClazz.getGenericSuperclass();
                //获取这样可以得到泛型了
                //因为泛型可能不止一个,所以返回的是数组,所以我们取第一个,
                return genericSuperclass.getActualTypeArguments()[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Object.class;
    }
}
