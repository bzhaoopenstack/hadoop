/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.UTF8;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;

/** 
 * DatanodeInfo represents the status of a DataNode.
 *
 * @author Mike Cafarella
 * @author Konstantin Shvachko
 */
public class DatanodeInfo extends DatanodeID implements Writable {
  protected long capacity;
  protected long remaining;
  protected long lastUpdate;

  DatanodeInfo() {
    this( new String(), new String() );
  }
  
  DatanodeInfo( String name, String storageID) {
    super( name, storageID );
    this.capacity = 0L;
    this.remaining = 0L;
    this.lastUpdate = 0L;
  }
  
  /** The raw capacity. */
  public long getCapacity() { return capacity; }

  /** The raw free space. */
  public long getRemaining() { return remaining; }

  /** The time when this information was accurate. */
  public long getLastUpdate() { return lastUpdate; }

  /** @deprecated Use {@link #getLastUpdate()} instead. */
  public long lastUpdate() { return getLastUpdate(); }

  /** A formatted string for reporting the status of the DataNode. */
  public String getDatanodeReport() {
    StringBuffer buffer = new StringBuffer();
    long c = getCapacity();
    long r = getRemaining();
    long u = c - r;
    buffer.append("Name: "+name+"\n");
    buffer.append("Total raw bytes: "+c+" ("+DFSShell.byteDesc(c)+")"+"\n");
    buffer.append("Used raw bytes: "+u+" ("+DFSShell.byteDesc(u)+")"+"\n");
    buffer.append("% used: "+DFSShell.limitDecimal(((1.0*u)/c)*100,2)+"%"+"\n");
    buffer.append("Last contact: "+new Date(lastUpdate)+"\n");
    return buffer.toString();
  }

  /////////////////////////////////////////////////
  // Writable
  /////////////////////////////////////////////////
  static {                                      // register a ctor
    WritableFactories.setFactory
      (DatanodeInfo.class,
       new WritableFactory() {
         public Writable newInstance() { return new DatanodeInfo(); }
       });
  }

  /**
   */
  public void write(DataOutput out) throws IOException {
    new UTF8( this.name ).write(out);
    new UTF8( this.storageID ).write(out);
    out.writeLong(capacity);
    out.writeLong(remaining);
    out.writeLong(lastUpdate);
  }

  /**
   */
  public void readFields(DataInput in) throws IOException {
    UTF8 uStr = new UTF8();
    uStr.readFields(in);
    this.name = uStr.toString();
    uStr.readFields(in);
    this.storageID = uStr.toString();
    this.capacity = in.readLong();
    this.remaining = in.readLong();
    this.lastUpdate = in.readLong();
  }
}
