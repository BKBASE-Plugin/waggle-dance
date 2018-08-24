/**
 * Copyright (C) 2016-2018 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.bdp.waggledance.client;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.metastore.api.Database;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hotels.beeju.ThriftHiveMetaStoreJUnitRule;

@RunWith(MockitoJUnitRunner.class)
public class ThriftMetastoreClientManagerIntegrationTest {

  @Rule
  public ThriftHiveMetaStoreJUnitRule hive = new ThriftHiveMetaStoreJUnitRule("dbname");

  private final HiveConf hiveConf = new HiveConf();
  private ThriftMetastoreClientManager manager;

  @Before
  public void init() throws Exception {
    hiveConf.setVar(ConfVars.METASTOREURIS, hive.getThriftConnectionUri());
    manager = new ThriftMetastoreClientManager(hiveConf);
  }

  @Test
  public void open() throws Exception {
    manager.open();

    Database database = manager.getClient().get_database("dbname");
    assertNotNull(database);
  }

  @Test
  public void reconnnect() throws Exception {
    manager.reconnect();

    Database database = manager.getClient().get_database("dbname");
    assertNotNull(database);
  }

  @Test
  public void openWithDummyConnectionThrowsRuntimeWithOriginalExceptionInMessage() throws Exception {
    hiveConf.setVar(ConfVars.METASTOREURIS, "thrift://localhost:123");
    manager = new ThriftMetastoreClientManager(hiveConf);

    try {
      manager.open();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), containsString("java.net.ConnectException: Connection refused"));
    }
  }

}
