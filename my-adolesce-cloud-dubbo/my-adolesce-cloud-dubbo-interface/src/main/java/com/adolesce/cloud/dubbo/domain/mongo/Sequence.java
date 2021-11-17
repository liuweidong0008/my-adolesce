package com.adolesce.cloud.dubbo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sequence {
    //主键
    private ObjectId id;
    //自增序列
    private long seqId;
    //集合名称
    private String collName;
}