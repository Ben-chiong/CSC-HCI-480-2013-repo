package edu.oswego.csc480_hci521_2013.client.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import edu.oswego.csc480_hci521_2013.client.events.RFGenerateEvent;
import edu.oswego.csc480_hci521_2013.client.events.RFParameterEvent;
import edu.oswego.csc480_hci521_2013.client.events.RFParameterEventHandler;
import edu.oswego.csc480_hci521_2013.client.events.RFProgressEvent;
import edu.oswego.csc480_hci521_2013.client.events.RFProgressEventHandler;
import edu.oswego.csc480_hci521_2013.client.events.TreeVisEvent;
import edu.oswego.csc480_hci521_2013.client.place.DoublePanelPlace;
import edu.oswego.csc480_hci521_2013.client.place.DoublePanelPlace.Location;
import edu.oswego.csc480_hci521_2013.client.place.popout.DataPanelPlace;
import edu.oswego.csc480_hci521_2013.client.place.popout.PopoutPlace.PopoutType;
import edu.oswego.csc480_hci521_2013.client.presenters.DataPanelPresenter;
import edu.oswego.csc480_hci521_2013.client.presenters.RfParametersPresenter;
import edu.oswego.csc480_hci521_2013.client.presenters.RfParametersPresenterImpl;
import edu.oswego.csc480_hci521_2013.client.services.H2OServiceAsync;
import edu.oswego.csc480_hci521_2013.client.services.RFViewPoller;
import edu.oswego.csc480_hci521_2013.client.ui.RfParametersViewImpl;
import edu.oswego.csc480_hci521_2013.shared.h2o.json.RF;
import edu.oswego.csc480_hci521_2013.shared.h2o.json.RFView;
import edu.oswego.csc480_hci521_2013.shared.h2o.json.ResponseStatus;
import edu.oswego.csc480_hci521_2013.shared.h2o.urlbuilders.RFBuilder;
import edu.oswego.csc480_hci521_2013.shared.h2o.urlbuilders.RFViewBuilder;

public class DataPanelActivity extends AbstractPanelActivity implements
        DataPanelPresenter {

    static final Logger logger = Logger.getLogger(DataPanelActivity.class
            .getName());
    private RF randomForest;
    private PlaceController places;
    EventBus eventBus;
    View view;
    H2OServiceAsync h2oService;
    String[] dataKeys;
    boolean isPopout = false;

    public DataPanelActivity(Location loc, DoublePanelPlace place, EventBus eventBus, H2OServiceAsync service, View panelView, PlaceController places) {
        super(loc);
        this.view = panelView;
        this.eventBus = eventBus;
        this.h2oService = service;
        this.places = places;
        dataKeys = place.getDataKeys();
    }

    public DataPanelActivity(DataPanelPlace place, EventBus eventBus, H2OServiceAsync service, View panelView, PlaceController places) {
        this.view = panelView;
        this.eventBus = eventBus;
        this.h2oService = service;
        this.places = places;
        dataKeys = new String[]{place.getDataKey()};
        isPopout = true;
    }
    
	//==========================================================================
	// Activity lifecycle methods
	//==========================================================================
    
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        this.view.setPresenter(this);
        panel.setWidget(view);
        bind();

        // Add/remove any data tabs if necessary
        if (isPopout()) {
            addDataTab(dataKeys[0]);
        } else {
            List<Integer> toBeAdded = new ArrayList<Integer>();
            List<Integer> toBeRemoved = new ArrayList<Integer>();
            for (int i = 0; i < dataKeys.length; i++) {
                if (view.getTabCount() - 1 < i) {
                    toBeAdded.add(i);
                    continue;
                }
                if (!dataKeys[i].equals(view.getTab(i).getDataKey())) {
                    toBeRemoved.add(i);
                    toBeAdded.add(i);
                }
            }
            if (view.getTabCount() > dataKeys.length) {
                int count = view.getTabCount() - dataKeys.length;
                for (int i = view.getTabCount() - 1; i > view.getTabCount() - 1 - count; i--) {
                    toBeRemoved.add(i);
                }
            }
            for (int i : toBeRemoved) {
                view.removeDataTab(i);
            }
            for (int i : toBeAdded) {
                addDataTab(dataKeys[i]);
            }

            logger.log(Level.INFO, "COUNT:" + toBeAdded.size());
        }

    }

    @Override
    public boolean isPopout() {
        return isPopout;
    }

    @Override
    public void popPanel(IsWidget panel) {
        view.removeDataTab(view.getActiveTabIndex());
    }

    @Override
    public void addPanel(IsWidget panel, String title) {
        view.addDataTab(title, (TabView) panel);
    }
    
	//==========================================================================
	// Presenter methods
	//==========================================================================

    @Override
    public Command getGenerateCommand() {
        return new Command() {
            @Override
            public void execute() {
                logger.log(Level.INFO, "Generating Forest");
                generateForestPopup();
            }
        };
    }

    @Override
    public Command getTreeVisCommand(final int index) {
        return new Command() {
            @Override
            public void execute() {
                eventBus.fireEvent(new TreeVisEvent(randomForest, index));
            }
        };
    }

    @Override
    public Command getPopOutCommand() {
        return new Command() {
            @Override
            public void execute() {
                DataPanelPlace place = new DataPanelPlace();
                TabView active = view.getTab(view.getActiveTabIndex());
                place.setDataKey(dataKeys[view.getActiveTabIndex()]);
                popOut(place, active, active.getDataKey());
            }
        };
    }

    @Override
    public Command getCloseCommand() {
        return new Command() {
            @Override
            public void execute() {
                DoublePanelPlace place = ((DoublePanelPlace) places.getWhere()).clone();
                place.removeDataKey(view.getActiveTabIndex());
                goTo(place);
            }
        };
    }

	//==========================================================================
	// Helper methods
	//==========================================================================
    
    private void bind() {
        
        eventBus.addHandler(RFProgressEvent.TYPE, new RFProgressEventHandler() {
            @Override
            public void onDataUpdate(RFProgressEvent e) {
                if (isOurData(e.getData())) {
                    RFView rfview = e.getData();
                    logger.log(Level.INFO, rfview.toString());
                    ResponseStatus status = rfview.getResponse();
                    if (status.isPoll()) {
                        int done = status.getProgress();
                        int total = status.getProgressTotal();
                        logger.log(Level.INFO, "Trees: Generated " + done + " of " + total);
                        view.getTab(view.getActiveTabIndex()).setForestStatus(done, total);
                    }
                    else {
                        logger.log(Level.INFO, "Forest finished");
                        view.getTab(view.getActiveTabIndex()).forestFinish(rfview.getNtree());
                    }
                }
            }
        });

        eventBus.addHandler(RFParameterEvent.TYPE, new RFParameterEventHandler() {
            @Override
            public void onParams(RFParameterEvent event) {
                RFBuilder builder = event.getBuilder();
                h2oService.generateRandomForest(builder, new AsyncCallback<RF>() {
                    @Override
                    public void onFailure(Throwable thrwbl) {
                        logger.log(Level.SEVERE, thrwbl.toString());
                        // FIXME: do a message box or something...
                    }

                    @Override
                    public void onSuccess(RF rf) {
                        logger.log(Level.INFO, "Forest Started");
                        randomForest = rf;

                        eventBus.fireEvent(new RFGenerateEvent(rf));

                        view.getTab(view.getActiveTabIndex()).forestStarted();

                        new RFViewPoller(eventBus, h2oService, randomForest).start();
                    }
                });
            }
        });
        
    }

    private boolean isOurData(RFView data) {
        if (data.getDataKey().equals(randomForest.getDataKey())
                && data.getModelKey().equals(randomForest.getModelKey())) {
            return true;
        }
        return false;
    }

    public void addDataTab(final String dataKey) {
        this.h2oService.getParsedData(dataKey,
                new AsyncCallback<List<Map<String, String>>>() {
            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, caught.toString());
                // FIXME: do a message box or something...
            }

            @Override
            public void onSuccess(List<Map<String, String>> result) {
                view.addDataTab(dataKey, result);
            }
        });
    }

    private void startRFViewPoller() {
        // TODO: this should probably be somewhere else...
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            boolean isFinished = false;

            @Override
            public boolean execute() {
                if (isFinished) {
                    logger.log(Level.INFO,
                            "Polling forest generation has finished");
                    return false;
                }

                logger.log(Level.INFO, "Polling forest generation progress");
                h2oService.getRandomForestView(new RFViewBuilder(randomForest),
                        new AsyncCallback<RFView>() {
                    @Override
                    public void onFailure(Throwable thrwbl) {
                        logger.log(Level.SEVERE, thrwbl.toString());
                    }

                    @Override
                    public void onSuccess(RFView rfview) {
                        if (!rfview.getResponse().isPoll()) {
                            isFinished = true;
                        }
                        eventBus.fireEvent(new RFProgressEvent(rfview));
                    }
                });
                return true;
            }
        }, 1000);
    }

    public void goTo(Place place) {
        this.places.goTo(place);
    }
    
    private void generateForest() {
        h2oService.generateRandomForest(new RFBuilder(dataKeys[view.getActiveTabIndex()]),
                new AsyncCallback<RF>() {
            @Override
            public void onFailure(Throwable thrwbl) {
                logger.log(Level.SEVERE, thrwbl.toString());
                // FIXME: do a message box or something...
            }

            @Override
            public void onSuccess(RF rf) {
                logger.log(Level.INFO, "Forest Started");
                randomForest = rf;

                eventBus.fireEvent(new RFGenerateEvent(rf));

                view.getTab(view.getActiveTabIndex()).forestStarted();
                startRFViewPoller();
            }
        });
    }

    private void generateForestPopup() {
        String datakey = dataKeys[view.getActiveTabIndex()];
        final RfParametersPresenter popUp = new RfParametersPresenterImpl(datakey, new RfParametersViewImpl(), eventBus);

        //Call H2OService.getColumnHeaders
        h2oService.getColumnHeaders(datakey, new AsyncCallback<ArrayList<String>>() {
            @Override
            public void onFailure(Throwable thrwbl) {
                logger.log(Level.SEVERE, thrwbl.toString());
                // FIXME: do a message box or something...
            }

            @Override
            public void onSuccess(ArrayList<String> columnHeaders) {
                logger.log(Level.INFO, "Headers received");
                popUp.setHeaders(columnHeaders);
            }
        });

        popUp.getView().showPopUp();
    }
    
}