// Copyright 2013 State University of New York at Oswego
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package edu.oswego.csc480_hci521_2013.client.ui;

import edu.oswego.csc480_hci521_2013.client.presenters.adapters.ConfusionMatrixAdapter;
import edu.oswego.csc480_hci521_2013.shared.h2o.json.ConfusionMatrix;
import static org.mockito.Mockito.when;

import edu.oswego.csc480_hci521_2013.shared.h2o.json.RFView;
import edu.oswego.csc480_hci521_2013.shared.h2o.json.ResponseStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConfusionMatrixAdapterTest {

    @Mock
    private RFView randomForest;
    @Mock
    private ConfusionMatrixView matrixView;
    @Mock
    private ResponseStatus status;
    @Mock
    private RFView.TreeProperties tree;
    @Mock
    private ConfusionMatrix matrix;

    public ConfusionMatrixAdapterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(randomForest.getResponse()).thenReturn(status);
        when(randomForest.getTrees()).thenReturn(tree);
        when(randomForest.getConfusionMatrix()).thenReturn(matrix);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getResponseVariableTest()
    {
        when(randomForest.getResponseVariable()).thenReturn(7);
        ConfusionMatrixAdapter adapter = new ConfusionMatrixAdapter(randomForest);
        assertEquals(adapter.getResponseVariable(), "7");
    }

    @Test
    public void getNtreeTest()
    {
        when(randomForest.getNtree()).thenReturn(1000);
        ConfusionMatrixAdapter adapter = new ConfusionMatrixAdapter(randomForest);
        assertEquals(adapter.getNtree(), "1000");
    }

    @Test
    public void getMtryTest()
    {
        when(randomForest.getMtry()).thenReturn(9);
        ConfusionMatrixAdapter adapter = new ConfusionMatrixAdapter(randomForest);
        assertEquals(adapter.getMtry(), "9");
    }

    @Test
    public void getMtryAllTest()
    {
        when(randomForest.getMtry()).thenReturn(-1);
        ConfusionMatrixAdapter adapter = new ConfusionMatrixAdapter(randomForest);
        assertEquals(adapter.getMtry(), "All");
    }

    @Test
    public void getConfusionMatrixTest()
    {
        List<List<Integer>> scores = new ArrayList<List<Integer>>(Arrays.asList(
                new ArrayList<Integer>(Arrays.asList(1,0,0)),
                new ArrayList<Integer>(Arrays.asList(0,1,0)),
                new ArrayList<Integer>(Arrays.asList(0,0,1))
                ));

        when(matrix.getScores()).thenReturn(scores);
        when(randomForest.getConfusionMatrix()).thenReturn(matrix);
        ConfusionMatrixAdapter adapter = new ConfusionMatrixAdapter(randomForest);

        List<List<Integer>> scoresList = adapter.getScores();
        assertEquals(3, scoresList.size());
        assertEquals(3, scoresList.get(0).size());
        assertEquals(3, scoresList.get(1).size());
        assertEquals(3, scoresList.get(2).size());

        assertEquals(scoresList.get(0).get(0), Integer.valueOf(1));
        assertEquals(scoresList.get(0).get(1), Integer.valueOf(0));
        assertEquals(scoresList.get(0).get(2), Integer.valueOf(0));

        assertEquals(scoresList.get(1).get(0), Integer.valueOf(0));
        assertEquals(scoresList.get(1).get(1), Integer.valueOf(1));
        assertEquals(scoresList.get(1).get(2), Integer.valueOf(0));

        assertEquals(scoresList.get(2).get(0), Integer.valueOf(0));
        assertEquals(scoresList.get(2).get(1), Integer.valueOf(0));
        assertEquals(scoresList.get(2).get(2), Integer.valueOf(1));
    }
}