/*
 * Copyright 2013 State University of New York at Oswego
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

package edu.oswego.csc480_hci521_2013.shared.h2o;

public class RFRequestImpl implements RFRequest {

    private String dataKey;
    private String responseVariable;
    private int numberTrees;
    private boolean oob;
    private int numberFeatures;

    public RFRequestImpl() {
        this.dataKey = "cars.hex";
    }

    public String getDataKey() {
        return this.dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getResponseVariable() {
        return this.responseVariable;
    }
    public void setResponseVariable(String responseVariable) {
        this.responseVariable = responseVariable;
    }

    public int getNumberTrees() {
        return this.numberTrees;
    }
    public void setNumberTrees(int nTrees) {
        this.numberTrees = nTrees;
    }

    public boolean getOutOfBagErrors() {
        return this.oob;
    }

    public void setOutOfBagerrors(boolean oob) {
        this.oob = oob;
    }

    public int getNumberFeatures() {
        return this.numberFeatures;
    }

    public void setNumberFeatures(int numberFeatures) {
        this.numberFeatures = numberFeatures;
    }
}