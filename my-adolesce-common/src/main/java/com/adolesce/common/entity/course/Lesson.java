package com.adolesce.common.entity.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/5 19:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    /**
     * 课程编号
     */
    private String lessonNo;
    /**
     * 课程名称
     */
    private String lessonName;
    /**
     * 课程时长（天）
     */
    private Integer lessonTime;
    /**
     * 是否大纲
     */
    private boolean isOutline;
    /**
     * 课程明细
     */
    private List<String> lessonDetails = new ArrayList<>();
    /**
     * 基础课程明细
     */
    private static List<Lesson> basicLessons;
    /**
     * 就业课程明细
     */
    private static List<Lesson> seniorLessons;
    /**
     * 法定节假日
     */
    private static Map<String,String> holidays;

    static {
        readLessonMsgFromConfig("config/txt/lessons.txt");
        readHolidayMsgFromConfig("config/txt/holiday.txt");
    }

    private static void readHolidayMsgFromConfig(String configPath) {
        BufferedReader in;
        try {
            ClassPathResource configFile = new ClassPathResource(configPath);
            in = new BufferedReader(new FileReader(configFile.getFile()));
            String line;
            holidays = new HashMap<>();
            while ((line = in.readLine()) != null) {
                if (StringUtils.isEmpty(line))
                    continue;
                if (StringUtils.equals("END", line))
                    break;
                line = line.trim();
                holidays.put(line.split(":")[0],line.split(":")[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readLessonMsgFromConfig(String configPath) {
        BufferedReader in;
        try {
            ClassPathResource configFile = new ClassPathResource(configPath);
            in = new BufferedReader(new FileReader(configFile.getFile()));
            String line;
            Lesson lesson = new Lesson();
            String lessonNo = "";

            while ((line = in.readLine()) != null) {
                if (StringUtils.isEmpty(line))
                    continue;
                if (StringUtils.equals("END",line))
                    break;

                if (seniorLessons == null) {
                    if (!line.startsWith(" ")&& line.contains("基础班")) {
                        basicLessons = new ArrayList<>();
                    } else if (!line.startsWith(" ") && line.contains("就业班")) {
                        seniorLessons = new ArrayList<>();
                    } else if (line.contains("|")){
                        lesson = new Lesson();
                        String[] contentArr = line.split("\\、")[1].split("\\|");
                        lesson.setLessonNo(contentArr[0]);
                        lesson.setLessonName(contentArr[1]);
                        lesson.setLessonTime(Integer.parseInt(contentArr[2]));
                        lesson.setOutline(Boolean.parseBoolean(StringUtils.trim(contentArr[3])));
                        basicLessons.add(lesson);
                    } else{
                        lesson.getLessonDetails().add(StringUtils.trim(line.split("\\、")[1]));
                    }
                }else{
                    if (line.contains("|")){
                        lesson = new Lesson();
                        String[] contentArr = line.split("\\、")[1].split("\\|");
                        lesson.setLessonNo(contentArr[0]);
                        lesson.setLessonName(contentArr[1]);
                        lesson.setLessonTime(Integer.parseInt(contentArr[2]));
                        lesson.setOutline(Boolean.parseBoolean(StringUtils.trim(contentArr[3])));
                        seniorLessons.add(lesson);
                    } else{
                        lesson.getLessonDetails().add(StringUtils.trim(line.split("\\、")[1]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*if (CollUtil.isNotEmpty(lesson.lessonDetails)) {
        lesson.setLessonTime(lesson.lessonDetails.size());
    }*/

    public static List<Lesson> getBasicLessons() {
        return basicLessons;
    }

    public static List<Lesson> getSeniorLessons() {
        return seniorLessons;
    }

    public static Map<String, String> getHolidays(String startDateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String,String> nowHolidays = new HashMap<>();
        Date startDate = dateFormat.parse(startDateStr);
        holidays.forEach((k,v) -> {
            try {
                if(dateFormat.parse(k).compareTo(startDate) >= 0){
                    nowHolidays.put(k,v);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return nowHolidays;
    }

    public static void main(String[] args) {
        List<Lesson> basicLessons = Lesson.getBasicLessons();
        List<Lesson> seniorLessons = Lesson.getSeniorLessons();
    }
}
