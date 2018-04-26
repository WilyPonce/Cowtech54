/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.sourcey.cowtech54;

import com.amazonaws.regions.Regions;

import java.util.Random;


public class Constants {

    public static final String IDENTITY_POOL_ID = "us-east-2:eef8597f-0fcd-4777-8689-896e0de23bb1";
    public static final Regions COGNITO_REGION = Regions.US_EAST_2;  // Set your Cognito region if is different

    // Note that spaces are not allowed in the table name
    public static final String TEST_TABLE_NAME = "Table_Name_Test";//"CHANGE_ME";
    public static final String TABLE_NAME = "Truck4.0";//"CHANGE_ME";

    public static final Regions DYNAMODB_REGION = Regions.US_EAST_2;  // Set your DynamoDB region if is different

    public static final Random random = new Random();
    public static final String[] NAMES = new String[] {
            "Norm", "Jim", "Jason", "Zach", "Matt", "Glenn", "Will", "Wade", "Trevor", "Jeremy",
            "Ryan", "Matty", "Steve", "Pavel"
    };

    public static String getRandomName() {
        int name = random.nextInt(NAMES.length);

        return NAMES[name];
    }

    public static int getRandomScore() {
        return random.nextInt(1000) + 1;
    }
}
