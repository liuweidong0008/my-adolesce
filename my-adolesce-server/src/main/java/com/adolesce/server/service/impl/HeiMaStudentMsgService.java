package com.adolesce.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.adolesce.cloud.dubbo.api.db.HeiMaStudentMsgApi;
import com.adolesce.cloud.dubbo.domain.db.HeiMaStudentMsg;
import com.adolesce.cloud.dubbo.dto.HeiMaStudentMsgDto;
import com.adolesce.common.entity.course.Lesson;
import com.adolesce.common.entity.course.Teacher;
import com.adolesce.common.entity.course.TimeTable;
import com.adolesce.common.utils.excel.export.ExcelExportHelper;
import com.adolesce.common.vo.Response;
import com.adolesce.server.dto.CourseExportParams;
import com.adolesce.server.utils.threadlocal.CurrentDateThreadLocalUtil;
import com.adolesce.server.utils.threadlocal.CurrentLessonCountUtil;
import com.adolesce.server.utils.threadlocal.CurrentTeacherUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/5 19:12
 */
@Service
public class HeiMaStudentMsgService {
    @DubboReference
    private HeiMaStudentMsgApi heiMaStudentMsgApi;
    @Autowired
    private ExcelService excelService;
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");

    public Response queryByParams(HeiMaStudentMsgDto studentMsgDto) throws ParseException {
        return heiMaStudentMsgApi.queryByParams(studentMsgDto);
    }

    public void saveStudentMsg(HeiMaStudentMsgDto studentMsgDto) throws ParseException {
        heiMaStudentMsgApi.saveStudentMsg(studentMsgDto);
    }

    public void deleteById(Long id) {
        heiMaStudentMsgApi.deleteById(id);
    }

    public Response queryById(HeiMaStudentMsgDto studentMsgDto) {
        return heiMaStudentMsgApi.queryById(studentMsgDto);
    }

    public Response importStyle(MultipartFile excelFile) throws Exception {
        Response response = excelService.importStyle1(excelFile);
        if (response.getSuccess()) {
            heiMaStudentMsgApi.saveBatch((List<HeiMaStudentMsg>) response.getData());
            return Response.success("导入成功！");
        } else {
            return response;
        }
    }

    /**
     * 导出课表
     *
     * @param response
     * @param params
     */
    public void courseExport(HttpServletResponse response, CourseExportParams params) throws ParseException {
        //1、设置开课日期
        Date currentDate = format1.parse(params.getStartDate());
        CurrentDateThreadLocalUtil.setCurrentDate(currentDate);
        //2、设置课程
        CurrentLessonCountUtil.setLessonCount(0);
        //3、设置授课老师
        CurrentTeacherUtil.setCurrentTeacher(Teacher.getTeacher(params.getTeacher()));

        List<TimeTable> timeTables;
        if ("基础".equals(params.getCourseType())) {
            //4、拉取所有基础课程大纲
            List<Lesson> basicLessons = Lesson.getBasicLessons();
            if (CollUtil.isEmpty(basicLessons)) {
                return;
            }
            //5、组装课表集合
            timeTables = this.getAllTimeTables(basicLessons, params);
        } else {
            //4、拉取所有就业课程大纲
            List<Lesson> seniorLessons = Lesson.getSeniorLessons();
            if (CollUtil.isEmpty(seniorLessons)) {
                return;
            }
            //6、组装课表集合
            timeTables = this.getAllTimeTables(seniorLessons, params);
        }
        //7、对课表进行日期、休息、自习、法定节假日等处理
        timeTables = this.handlerCuorses(timeTables,params.getStartDate());

        //8、导出课表（方式一：sheet名称代码指定）
        String batchName = params.getCourseType() + params.getJavaBatch() + "期";
        String batchDate = format2.format(currentDate);

        String[] tempSheetNames = {"Sheet1"};
        String[] sheetNames = {batchName};
        List dataLists = new ArrayList<>();
        dataLists.add(timeTables);
        String fileName = "长沙黑马JavaEE" + batchName + "（" + batchDate + "新型面授）.xlsx";
        ExcelExportHelper.export(tempSheetNames, sheetNames, dataLists, "courseTemp.xlsx", fileName, response);
    }


    /**
     * 对课表进行日期、休息、自习、法定节假日等处理
     * @param timeTables
     * @param startDate
     */
    private List<TimeTable> handlerCuorses(List<TimeTable> timeTables, String startDate) throws ParseException {
        Map<String, String> holidays = Lesson.getHolidays(startDate);
        List<TimeTable> resultCourses = new ArrayList<>();
        Integer count = 2;
        boolean isTwoTwo = true;
        String nextRemark = "休息";

        while(holidays.keySet().contains(CurrentDateThreadLocalUtil.getCurrentDateStr())){
            CurrentDateThreadLocalUtil.currentDateIncrement();
            continue;
        }

        for (TimeTable timeTable:timeTables){
            while(holidays.keySet().contains(CurrentDateThreadLocalUtil.getCurrentDateStr())){
                TimeTable table = new TimeTable();
                table.setDate(CurrentDateThreadLocalUtil.getCurrentDateStr());
                table.setRemark(holidays.get(CurrentDateThreadLocalUtil.getCurrentDateStr()));
                resultCourses.add(table);
                //当前日期自增一天、小阶段课程天数清空、修改下一次备注
                CurrentDateThreadLocalUtil.currentDateIncrement();
                CurrentLessonCountUtil.setLessonCount(0);
                nextRemark = "自习";
                continue;
            }

            timeTable.setDate(CurrentDateThreadLocalUtil.getCurrentDateStr());
            if("是".equals(timeTable.getIsOutline())){
                CurrentLessonCountUtil.lessonCountIncrement();
            }
            if("考试".equals(timeTable.getLessonNo())){
                timeTable.setLessonNo("");
                timeTable.setRemark("考试");
            }
            resultCourses.add(timeTable);
            CurrentDateThreadLocalUtil.currentDateIncrement();
            //到达自习或者休息
            if(CurrentLessonCountUtil.getLessonCount() == count){
                TimeTable table = new TimeTable();
                table.setDate(CurrentDateThreadLocalUtil.getCurrentDateStr());
                table.setRemark(nextRemark);
                if(holidays.keySet().contains(CurrentDateThreadLocalUtil.getCurrentDateStr())){
                    table.setRemark(holidays.get(CurrentDateThreadLocalUtil.getCurrentDateStr()));
                }else{
                    table.setRemark(nextRemark);
                }
                resultCourses.add(table);
                //当前日期自增一天、小阶段课程天数清空、修改下一次备注
                CurrentDateThreadLocalUtil.currentDateIncrement();
                CurrentLessonCountUtil.setLessonCount(0);
                if(holidays.keySet().contains(CurrentDateThreadLocalUtil.getCurrentDateStr())){
                    nextRemark = "自习";
                }else{
                    if("休息".equals(nextRemark)){
                        nextRemark = "自习";
                    }else{
                        nextRemark = "休息";
                    }
                }
                if("集合".equals(timeTable.getLessonName()) ||
                        "SpringCloud".equals(timeTable.getLessonName())){
                    isTwoTwo = false;
                }
                if("项目二(CRM实战)".equals(timeTable.getLessonName())||
                    "SpringCloud实战".equals(timeTable.getLessonName())){
                    isTwoTwo = true;
                }
                if(isTwoTwo){
                    if(count == 3){
                        count = 2;
                    }
                }else{
                    if(count == 2){
                        count = 3;
                    }else{
                        count = 2;
                    }
                }
            }
        }
        //节假日前一天不上晚自习
        Map<String,TimeTable> resultCourseMap = resultCourses.stream().collect(Collectors.toMap(t -> t.getDate(), Function.identity()));
        holidays.keySet().forEach(holiday -> {
            try {
                String neetHandlerDateStr = format1.format(DateUtil.offsetDay(format1.parse(holiday), -1));
                TimeTable timeTable = resultCourseMap.get(neetHandlerDateStr);
                if(ObjectUtil.isNotNull(timeTable)){
                    timeTable.setNightTeacherWorkNo("");
                    timeTable.setNightTeacherName("");
                    timeTable.setHaveNightCost("");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return resultCourses;
    }

    /**
     * 获得所有课表
     *
     * @param lessons
     * @param params
     * @return 课表集合
     */
    private List<TimeTable> getAllTimeTables(List<Lesson> lessons, CourseExportParams params) {
        //循环课表大纲
        List<String> lessonDetails;
        List<TimeTable> timeTabes;
        List<TimeTable> allTimeTables = new ArrayList<>();

        for (Lesson lesson : lessons) {
            lessonDetails = lesson.getLessonDetails();
            //课程明细为空的情况
            if (CollUtil.isEmpty(lessonDetails)) {
                timeTabes = getTimeTablesWitNoHaveDetail(lesson, params.getClassRoom());
                //课程明细不为空的情况
            } else {
                timeTabes = getTimeTablesWithHaveDetail(lesson, params.getClassRoom());
            }
            allTimeTables.addAll(timeTabes);
        }
        return allTimeTables;
    }

    /**
     * 获取课程课表（有课程明细）
     *
     * @param lesson
     * @param classRoom
     * @return
     */
    private List<TimeTable> getTimeTablesWithHaveDetail(Lesson lesson, String classRoom) {
        List<TimeTable> timeTabels = new ArrayList<>();
        TimeTable timeTable;
        for (String lessonName : lesson.getLessonDetails()) {
            timeTable = new TimeTable();
            //timeTable.setDate(CurrentDateThreadLocalUtil.currentDateIncrement()); //日期
            timeTable.setLessonNo(lesson.getLessonNo());  //课程编号
            timeTable.setLessonName(lessonName); //课程名称
            timeTable.setIsOutline(lesson.isOutline()?"是":"否"); //是否大纲
            timeTable.setLessonModel("传统全天");  //上课模式
            timeTable.setClassRoom("长沙新" + classRoom);  //上课教室
            timeTable.setTeacherName(CurrentTeacherUtil.getCurrentTeacher().getName()); //老师姓名
            timeTable.setTeacherWorkNo(CurrentTeacherUtil.getCurrentTeacher().getWorkNo()); //工号
            timeTable.setHaveTipsayCost("有"); //代课费
            //此处暂时全赋值，后续法定节假日前一天以下三个字段要赋空
            timeTable.setNightTeacherName(CurrentTeacherUtil.getCurrentTeacher().getName()); //晚自习辅导老师姓名
            timeTable.setNightTeacherWorkNo(CurrentTeacherUtil.getCurrentTeacher().getWorkNo()); //晚自习辅导老师工号
            timeTable.setHaveNightCost("有"); //有无晚自习补贴
            timeTabels.add(timeTable);
        }
        return timeTabels;
    }

    /**
     * 获取课程课表（无课程明细）
     *
     * @param lesson
     * @param classRoom
     * @return
     */
    private List<TimeTable> getTimeTablesWitNoHaveDetail(Lesson lesson, String classRoom) {
        List<TimeTable> timeTabels = new ArrayList<>();
        if (lesson.getLessonTime() != 0) {
            for (int i = 1; i <= lesson.getLessonTime(); i++) {
                TimeTable timeTable = new TimeTable();
                //timeTable.setDate(CurrentDateThreadLocalUtil.currentDateIncrement()); //日期
                //此处判断如果是休息或者自习需要设置备注，然后continue跳过
                //timeTable.setRemark("");  //备注
                timeTable.setLessonNo(lesson.getLessonNo());  //课程编号
                timeTable.setClassRoom("长沙新" + classRoom);  //上课教室
                timeTable.setTeacherName(CurrentTeacherUtil.getCurrentTeacher().getName()); //老师姓名
                timeTable.setTeacherWorkNo(CurrentTeacherUtil.getCurrentTeacher().getWorkNo()); //工号
                if (lesson.isOutline()) {
                    timeTable.setLessonName(lesson.getLessonName()); //课程名称
                    timeTable.setIsOutline("是"); //是否大纲
                    timeTable.setLessonModel("传统全天");  //上课模式
                    timeTable.setHaveTipsayCost("有"); //代课费
                    //此处暂时全赋值，后续法定节假日前一天以下三个字段要赋空
                    timeTable.setNightTeacherName(CurrentTeacherUtil.getCurrentTeacher().getName()); //晚自习辅导老师姓名
                    timeTable.setNightTeacherWorkNo(CurrentTeacherUtil.getCurrentTeacher().getWorkNo()); //晚自习辅导老师工号
                    timeTable.setHaveNightCost("有"); //有无晚自习补贴
                } else {
                    timeTable.setIsOutline("否"); //是否大纲
                }
                timeTabels.add(timeTable);
            }
        }
        return timeTabels;
    }
}
