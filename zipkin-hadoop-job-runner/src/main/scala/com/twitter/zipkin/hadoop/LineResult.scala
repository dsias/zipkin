/*
* Copyright 2012 Twitter Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.twitter.zipkin.hadoop

/**
 * This class represents a single line generated by a Hadoop job
 * @param line the single line of data
 */
abstract class LineResult(var line: List[String]) {

  checkIsValid()

  /**
   * Throws an exception of the line of data is malformed
   */
  def checkIsValid()

  /**
   * Gets the key of the line of data
   * @return the key
   */
  def getKey(): String

  /**
   * Gets the value for the line of data
   * @return the value associated to this line of data, as a list of strings tokenized by tabs
   */
  def getValue(): List[String]

  /**
   * Returns the value of the line as a String
   * @param sep the separator for the line
   * @return a String representation of the line
   */
  def getValueAsString(sep: String): String = {
    getValue().mkString(sep)
  }

  /**
   * Returns the value of the line as a String with elements separated by tabs
   * @return the value of the line
   */
  def getValueAsString(): String = {
    getValueAsString("\t")
  }

  override def toString(): String = {
    "(" + getKey() + ", [" + getValueAsString(", ") + "])"
  }

}

/**
 * A line result for Hadoop jobs whose keys are a single service
 * @param line the single line of data
 */
class PerServiceLineResult(line: List[String]) extends LineResult(line) {

  def checkIsValid() {
    if (line == null || line.isEmpty) {
      throw new IllegalArgumentException("Invalid input list: " + (if (line == null) "null" else line.mkString(", ")))
    }
  }

  def getKey() = {
    line.head
  }

  def getValue() = {
    line.tail
  }
}

/**
 * A line result for Hadoop jobs whose keys are pairs of services
 * @param line the single line of data
 */
class PerServicePairLineResult(line: List[String]) extends LineResult(line) {
  def checkIsValid() {
    if (line == null || line.length < 2) {
      throw new IllegalArgumentException("Invalid input list: " + (if (line == null) "null" else line.mkString(", ")))
    }
  }

  def getKey() = {
    line.head + HadoopJobClient.DELIMITER + line.tail.head
  }

  def getValue() = {
    line.tail.tail
  }
}