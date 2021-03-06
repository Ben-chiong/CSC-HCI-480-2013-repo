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
package edu.oswego.csc480_hci521_2013.client.presenters;

import java.util.List;
import com.google.gwt.user.client.ui.IsWidget;
import edu.oswego.csc480_hci521_2013.client.services.H2OServiceAsync;
import edu.oswego.csc480_hci521_2013.shared.h2o.json.Inspect.Column;
import edu.oswego.csc480_hci521_2013.shared.h2o.urlbuilders.RFBuilder;


/* Author: Mike Hayes
 *
 */
public interface RfParametersPresenter {

    View getView();
    void setHeaders(List<String> headers);
    public String getDataKey();
    H2OServiceAsync getH2OService();
    void fireRFParameterEvent(RFBuilder builder);

    public interface View extends IsWidget {

        void buildUi();

        //Displays the popup...
        void showPopUp();

        void setHeaders(List<String> headers);

        //A list of the data column headers.
        void setPresenter(RfParametersPresenter presenter);

        //Set error message to display.
        void setError(String error);

        void hidePopup();

        void setColumnInfo(Column[] cols);
    }
}
