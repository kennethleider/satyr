package com.canoeventures.satyr

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges}

class CommitterStream[A](stream : InputDStream[A]) {
   def withCommit(foreachFunc : RDD[A] => Unit) = {
      stream.foreachRDD { rdd =>
         val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
         foreachFunc(rdd)
         stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }
   }

   def ifNotEmpty(foreachFunc : RDD[A] => Unit) : RDD[A] => Unit = { rdd =>
      if (rdd.count() > 0) {
         foreachFunc(rdd)
      }
   }
}
