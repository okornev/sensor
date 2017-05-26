/*******************************************************************************
 *
 *   Copyright 2015 Walmart, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *******************************************************************************/
package com.oneops.sensor.ecv;

import com.oneops.ecv.health.Health;
import com.oneops.ecv.health.IHealth;
import com.oneops.ecv.health.IHealthCheck;
import com.oneops.ops.CiOpsProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


public class AppHealthCheck implements IHealthCheck {
    private static final long DEFAULT_CID = Long.valueOf(System.getProperty("sensor.defaultCiD", "1"));
    private static final  Logger logger = Logger.getLogger(AppHealthCheck.class);
    @Autowired
    private CiOpsProcessor coProcessor;
    @Autowired
    private Health okHealth;

    @Autowired
    private Health failedHealth;

    @Override
    public IHealth getHealth() {
        IHealth health = failedHealth;
        try {
            @SuppressWarnings("unused")
            String ciState = coProcessor.getCIstate(DEFAULT_CID);
            health = okHealth;
        } catch (Throwable e) {
            logger.error("Exception occurred determining health", e);
            health = new Health(HttpStatus.INTERNAL_SERVER_ERROR.value(), Boolean.FALSE, e.getMessage(), getName());
        }
        return health;
    }

    @Override
    public String getName() {
        return "sensor.Health";
    }
}
