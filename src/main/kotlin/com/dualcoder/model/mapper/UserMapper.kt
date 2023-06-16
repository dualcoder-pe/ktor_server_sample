package com.dualcoder.model.mapper

import com.dualcoder.model.vo.UserRef
import com.dualcoder.model.vo.UserVO
import org.bson.types.ObjectId

fun UserVO.ref(): UserRef {
    return UserRef(ObjectId(id), username)
}