package com.pt.vx.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.pt.vx.domain.weather.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;


public class WeatherUtil {

    //在https://lbs.amap.com/api/webservice/guide/create-project/get-key获取key
    private static final String KEY = "高德地图的KEY";//改成你的key

    private static final Logger log = Logger.getAnonymousLogger();

    private static final  String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";

    private static final  String CODE_URL = "https://restapi.amap.com/v3/geocode/geo";

    public static final String TYPE_LIVE = "base";
    public static final String TYPE_ALL = "all";

    public static WeatherResponseDto getWeather(String address, String city,String type){
        Code code = getCode(address, city);
        if(code == null){
            log.warning("获取区域编码失败，区域为空");
            return null;
        }
        String adCode = code.getAdcode();
        return getWeather(adCode, type);
    }

    public static WeatherResponseDto getWeather(String cityCode,String type){

        HashMap<String,Object> map = new HashMap<>();
        map.put("key",KEY);
        map.put("city",cityCode);
        map.put("extensions",type);
        String result = HttpUtil.get(WEATHER_URL,map);
        WeatherResponseDto weatherResponseDto =  JSONUtil.toBean(result, WeatherResponseDto.class);
        if(Objects.equals(0,weatherResponseDto.getStatus()) ||  !"10000".equals(weatherResponseDto.getInfocode())){
            log.warning("获取天气失败");
            return null;
        }
        return weatherResponseDto;
    }

    public static Code getCode(String address, String city){

        HashMap<String,Object> map = new HashMap<>();
        map.put("key",KEY);
        map.put("address",address);
        map.put("city",city);
        String result = HttpUtil.get(CODE_URL,map);
        CodeResponseDto codeResponseDto = JSONUtil.toBean(result, CodeResponseDto.class);
        if(Objects.equals(0,codeResponseDto.getStatus()) ){
            log.warning("获取区域编码失败:"+codeResponseDto.getInfo());
            return null;
        }
        if(CollectionUtil.isEmpty(codeResponseDto.getGeocodes())){
            log.warning("获取区域编码失败，区域为空");
            return null;
        }
        return codeResponseDto.getGeocodes().stream().findAny().orElse(null);
    }

}