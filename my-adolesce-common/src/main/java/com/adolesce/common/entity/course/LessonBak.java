package com.adolesce.common.entity.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/5 19:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonBak {
    /**
     * 课程编号
     */
    private String lessonNo;
    /**
     * 课程名称
     */
    private String lessonName;
    /**
     * 是否大纲
     */
    private boolean isOutline;
    /**
     * 基础课程明细
     */
    private static List<LessonBak> basicLessons;
    /**
     * 就业课程明细
     */
    private static List<LessonBak> seniorLessons;
    /**
     * 法定节假日
     */
    private static Map<String, String> holidays;

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
                holidays.put(line.split(":")[0], line.split(":")[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static void readLessonMsgFromConfig(String configPath) {
        BufferedReader in;
        try {
            ClassPathResource configFile = new ClassPathResource("config/txt/lessons.txt");
            in = new BufferedReader(new FileReader(configFile.getFile()));
            String line;
            LessonBak lesson = new LessonBak();
            String lessonNo = "";

            while ((line = in.readLine()) != null) {
                if (StringUtils.isEmpty(line))
                    continue;
                if (StringUtils.equals("END", line))
                    break;

                if (seniorLessons == null) {
                    if (!line.startsWith(" ") && line.contains("基础班")) {
                        basicLessons = new ArrayList<>();
                    } else if (!line.startsWith(" ") && line.contains("就业班")) {
                        seniorLessons = new ArrayList<>();
                    } else if (line.contains("|K")) {
                        lessonNo = line.split("\\|")[1];
                    } else {
                        lesson = new LessonBak();
                        if (line.contains("、")) {
                            line = line.split("\\、")[1];
                            lesson.setOutline(true);
                            lesson.setLessonNo(lessonNo);
                        } else {
                            lesson.setOutline(false);
                        }
                        line = line.trim();
                        lesson.setLessonName(line);
                        basicLessons.add(lesson);
                    }
                } else {
                    if (line.contains("|K")) {
                        lessonNo = line.split("\\|")[1];
                    } else {
                        lesson = new LessonBak();
                        if (line.contains("、")) {
                            line = line.split("\\、")[1];
                            lesson.setOutline(true);
                            lesson.setLessonNo(lessonNo);
                        } else {
                            lesson.setOutline(false);
                        }
                        line = line.trim();
                        lesson.setLessonName(line);
                        seniorLessons.add(lesson);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<LessonBak> getBasicLessons() {
        return basicLessons;
    }

    public static List<LessonBak> getSeniorLessons() {
        return seniorLessons;
    }

    public static Map<String, String> getHolidays() {
        return holidays;
    }

    public static void main(String[] args) {
        List<LessonBak> basicLessons = LessonBak.getBasicLessons();
        List<LessonBak> seniorLessons = LessonBak.getSeniorLessons();
    }
}
