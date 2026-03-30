package club.hsspace.hs.motorcontrol;


import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.text.ChoiceFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MainController {

    private SettingManage sc = new SettingManage();

    private Comm nm;


    @FXML
    private RadioButton radioDynamic;

    @FXML
    private Button sendData;

    @FXML
    private TreeView<String> channel;

    @FXML
    private Button setPid;

    @FXML
    private Button setStiff;

    @FXML
    private ComboBox<String> Stiff;

    @FXML
    private TextField pidMax;

    @FXML
    private RadioButton radioUser;

    @FXML
    private RadioButton radioPlugging;

    @FXML
    private Button contRun;

    @FXML
    private TextField pidP;

    @FXML
    private Button contReset;

    @FXML
    private TextField pidMin;

    @FXML
    private TextField pidK;

    @FXML
    private TextField stiffNum;

    @FXML
    private ImageView slider;

    @FXML
    private Button contCW;

    @FXML
    private Button connect;

    @FXML
    private TextField dataInput;

    @FXML
    private ListView<String> msgView;

    @FXML
    private Button contCMD;

    @FXML
    private ComboBox<String> interfaces;

    @FXML
    private RadioButton radioOpenLoop;

    @FXML
    private TextField contCMDValue;

    @FXML
    private RadioButton radioElectric;

    @FXML
    private Label errorNum;

    @FXML
    private TextField pidI;

    @FXML
    private TextField pidD;

    @FXML
    private Label actStiff;

    @FXML
    private Button clearMsg;

    @FXML
    private Button readPid;

    @FXML
    private RadioButton radioPosition;

    @FXML
    private ComboBox<String> connID;

    @FXML
    private RadioButton radioSpeed;

    @FXML
    private TextField contValue;

    @FXML
    private Button pauseLine;

    @FXML
    private Button clearLine;

    @FXML
    private HBox lineParent;

    @FXML
    private Label promptText;

    @FXML
    private ScrollBar hScroll;

    @FXML
    private ScrollBar vScroll;

    @FXML
    private Slider vSlider;

    @FXML
    private Slider hSlider;

    @FXML
    private Button recordSys;

    @FXML
    private Button recordSave;

    @FXML
    private TextField recordFile;

    @FXML
    private Label fileCount;

    @FXML
    private ProgressBar progress;

    @FXML
    private TextField chartX;

    @FXML
    private TextField chartY;

    private LineChart<Number, Number> dataChart;

    private NumberAxis xAxis;

    private NumberAxis yAxis;

    private AtomicBoolean isConnect = new AtomicBoolean(false);

    private AtomicBoolean isRun = new AtomicBoolean(false);

    private AtomicBoolean isCW = new AtomicBoolean(true);

    private AtomicBoolean isPauseLine = new AtomicBoolean(false);

    private AtomicReference<RadioButton> nowRadio = new AtomicReference<>();

    private List<RadioButton> modeRadio;

    private XYChart.Series[] seriesList = new XYChart.Series[]{new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series(),
            new XYChart.Series()};

    private byte getMode() {
        return (byte) (modeRadio.indexOf(nowRadio.get()) + 1);
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private void setPromptText(String msg) {
        Platform.runLater(() -> this.promptText.setText(msg));
    }

    @FXML
    void initialize() {

        // Initialize left radio buttons
        nowRadio.set(radioOpenLoop);
        ToggleGroup group = new ToggleGroup();
        modeRadio = List.of(radioOpenLoop, radioElectric, radioSpeed, radioPosition, radioDynamic, radioPlugging, radioUser);
        for (RadioButton radioButton : modeRadio) {
            radioButton.setToggleGroup(group);
            radioButton.setOnMouseClicked(mouseEvent -> {
                if (isConnect.get()) {
                    nowRadio.set(radioButton);
                    byte mode = getMode();
                    nm.sendData8((byte) 1, mode, (byte) ~mode, (byte) 0);
                }
            });
        }
        radioOpenLoop.setSelected(true);

        // Initialize line chart area
        initLineView();

        // Initialize right channel tree multi-selection menu
        initTreeView();

        // Initialize PID view
        initPIDView();

        // Initialize stiffness adjustment
        initStiffView();

        // Initialize data observation area
        initDataView();

        // Initialize connection control area
        initConnView();

        // Initialize motor control area
        initControlView();

        // Initialize record persistence area
        initRecordView();

        // Initialize fonts
        initFont();

    }


    public void initFont() {

        Font font12 = Font.loadFont(HelloApplication.class.getResourceAsStream("TNR.fon"), 12);
        pidK.setFont(font12);
        pidP.setFont(font12);
        pidI.setFont(font12);
        pidD.setFont(font12);
        pidMax.setFont(font12);
        pidMin.setFont(font12);
        stiffNum.setFont(font12);

        dataInput.setFont(font12);

        Font font20 = Font.loadFont(HelloApplication.class.getResourceAsStream("TNR.fon"), 20);
        contCMDValue.setFont(font20);
        contValue.setFont(font20);
    }

    private AtomicBoolean stopRecord = new AtomicBoolean(true);

    private int maxRecord = Integer.parseInt(sc.getProperty("record.unit", "3000"));

    private AtomicInteger recordCount = new AtomicInteger(0);

    private AtomicInteger recordFileCount = new AtomicInteger(0);

    private static SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private void initRecordView() {
        recordSys.setOnMouseClicked(v -> nm.sendASCII("sys=1"));

        recordSave.setOnMouseClicked(v -> {
            if (stopRecord.get()) {
                stopRecord.set(false);
                recordSave.setText("Stop Recording");
            } else {
                stopRecord.set(true);
                recordSave.setText("Save");
            }
        });
    }

    private FileOutputStream os;

    private void recordData(byte[] data) {
        if (stopRecord.get())
            return;

        if (os == null) {
            File file = new File(sc.getFile(), "data\\" + fileNameFormat.format(new Date()) + ".mvcs");
            try {
                boolean newFile = file.createNewFile();
                os = new FileOutputStream(file);
                Platform.runLater(() -> {
                    fileCount.setText("Current File Count: " + recordFileCount.incrementAndGet());
                    recordFile.setText(file.getName());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            os.write(data);
            Platform.runLater(() -> progress.setProgress(1.0d * recordCount.get() / maxRecord));

            if (recordCount.incrementAndGet() == maxRecord) {
                os.flush();
                os.close();
                os = null;
                recordCount.set(0);
                progress.setProgress(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reStart() {
        int value = Integer.parseInt(contValue.getText());
        if (!isCW.get())
            value *= -1;
        nm.sendData16(getMode(), value, (byte) 0x0);
        isRun.set(true);
        clearLine();
    }

    private void initControlView() {
        contReset.setOnMouseClicked(mouseEvent -> {
            nm.sendData8((byte) 0xF0, (byte) 0, (byte) 0xF0, (byte) 0);
        });
        contRun.setOnMouseClicked(mouseEvent -> {
            if (!isRun.get()) {
                reStart();
                contRun.setText("Stop");
            } else {
                nm.sendData16(getMode(), 0, (byte) 0x1);
                isRun.set(false);
                contRun.setText("Start");
            }
        });
        contCMD.setOnMouseClicked(mouseEvent -> {
            int cmd = Integer.parseInt(contCMDValue.getText());
            nm.sendData8((byte) 0x03, (byte) cmd, (byte) 0x0, (byte) 0x0);
        });
        contCW.setOnMouseClicked(mouseEvent -> {
            if (isCW.get()) {
                isCW.set(false);
                contCW.setText("Reverse");
            } else {
                isCW.set(true);
                contCW.setText("Forward");
            }
            reStart();
            contRun.setText("Stop");
        });
    }

    private void initPIDView() {
        readPid.setOnMouseClicked(mouseEvent -> {
            if (isConnect.get()) {
                nm.sendData8((byte) 0x02, getMode(), (byte) 0, (byte) 0);
            }
        });
        setPid.setOnMouseClicked(mouseEvent -> {
            if (isConnect.get()) {
                int k = (int) (Double.parseDouble(pidK.getText()) * 10000);
                int p = (int) (Double.parseDouble(pidP.getText()) * 10000);
                int i = (int) (Double.parseDouble(pidI.getText()) * 10000);
                int d = (int) (Double.parseDouble(pidD.getText()) * 10000);
                int max = (int) (Double.parseDouble(pidMax.getText()));
                int min = (int) (Double.parseDouble(pidMin.getText()));
                nm.sendData32(getMode(), k, p, i, d, max, min, 0);
            }
        });
    }

    private void initStiffView() {
        ObservableList<String> stiffoptions = FXCollections.observableArrayList("2500", "2000","1500","1000", "500","0");
        Stiff.setItems(stiffoptions);
        Stiff.getSelectionModel().selectFirst();

        //slider = new ImageView("file:/image/slider.png");
        slider.setTranslateX(-130);
        //String actS = sc.getProperty("ch.ch13", "NC");
        //actStiff.setText(actS);
        //aStiff.setText("1 2");
        setStiff.setOnMouseClicked(mouseEvent -> {
            String desiredStiff = Stiff.getSelectionModel().getSelectedItem();
            int sNum = (int) (Double.parseDouble(stiffNum.getText()));
            if (isConnect.get()) {
                if(stiffNum.getText().equals("2500")|stiffNum.getText().equals("2000")|stiffNum.getText().equals("1500")|
                        stiffNum.getText().equals("1000")|stiffNum.getText().equals("500")|stiffNum.getText().equals("0")) {
                    if(desiredStiff.equals("2500")) {
                        nm.sendASCII("stiff=2500");
                    }
                    else if(desiredStiff.equals("2000")) {
                        nm.sendASCII("stiff=2000");
                    }
                    else if(desiredStiff.equals("1500")) {
                        nm.sendASCII("stiff=1500");
                    }
                    else if(desiredStiff.equals("1000")) {
                        nm.sendASCII("stiff=1000");
                    }
                    else if(desiredStiff.equals("500")) {
                        nm.sendASCII("stiff=500");
                    }
                    else if(desiredStiff.equals("0")) {
                        nm.sendASCII("stiff=0");
                    }
                }
                else if(sNum>=0&sNum<=2500){
                    nm.sendASCII("stiff=" + String.valueOf(sNum));
                }
            }
        });
    }

    private void initDataView() {
        ObservableList<String> options = FXCollections.observableArrayList();
        msgView.setItems(options);

        clearMsg.setOnMouseClicked(v -> options.clear());

        sendData.setOnMouseClicked(v -> nm.sendASCII(dataInput.getText().trim()));
    }

    private List<int[]> chartData = new ArrayList<>();

    private
    DataInterface di = new DataInterface() {

        GeometryFactory gf = new GeometryFactory();

        int pLen = Integer.parseInt(sc.getProperty("chart.unit", "5"));

        double d = Double.parseDouble(sc.getProperty("chart.distance", "1000"));

        Coordinate[][] coordinates = new Coordinate[][]{new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen],
                new Coordinate[pLen]};
        int i = 0;

        @Override
        public synchronized void handleReceive(byte[] bytes, int[] data) {

            if(bytes[50] != 13 || bytes[51] != 10)
                return;

            recordData(bytes);
            setPromptText(dateFormat.format(new Date()) + " update Date");
            if (!isPauseLine.get()/* && hScroll.getValue() == hScroll.getMax()*/) {
                int times = data[14];
                for (int f = 0; f < 14; f++) {
                    coordinates[f][i] = new Coordinate(times, data[f]);
                }
                i++;
                if (i == pLen) {
                    //long st = System.currentTimeMillis();
                    for (int f = 0; f < 14; f++) {
                        Geometry geom = new LineString(new CoordinateArraySequence(coordinates[f]), gf);
                        Geometry simplified = DouglasPeuckerSimplifier.simplify(geom, d);

                        int finalF = f;
                        Coordinate[] res = simplified.getCoordinates();
                        //System.out.printf(res.length + " ");
                        Platform.runLater(() -> {
                            for (Coordinate each : res) {
                                ObservableList observableList = seriesList[finalF].getData();
                                observableList.add(new XYChart.Data(each.x, each.y));
                                int sizeS = seriesList[12].getData().size()-1; //last value of list
                                String stiffString = String.valueOf(seriesList[12].getData().get(sizeS).toString());

                                int sLen = stiffString.length();
                                //String stiffData = stiffString.substring(sLen-12,sLen-7);
                                String[] stiffD = stiffString.split(",");
                                String stiffData = stiffD[1];
                                actStiff.setText(stiffData);//realtime show stiff
                                double sliderP = Double.parseDouble(stiffData);
                                slider.setTranslateX(53.0/2500*sliderP-183);
                            }

                            int upper = Math.max(1000, times);
                            hSlider.setMax(upper);
                            hScroll.setMax(times - hSlider.getValue());

                            int value = (int) (hSlider.getValue());
                            int lower = Math.max(0, times - value);
                            xAxis.setLowerBound(lower);
                            hScroll.setValue(lower);
                            xAxis.setUpperBound(upper);
                            xAxis.setTickUnit((upper - lower) / 10);
                        });
                    }
                    //System.out.println("\n"+(System.currentTimeMillis() - st) );
                    i = 0;

                    Platform.runLater(() -> {
                        for (int f = 0; f < 14; f++) {
                            CheckBoxTreeItem<String> item = itemList.get(f);
                            if (item.isSelected()) {
                                item.setValue(names[f] + " | " + data[f]);
                            }
                            errorNum.setText(String.valueOf(data[13] >> 8));
                        }
                    });
                }



                /**
                 if (times % (10 * Integer.parseInt(sc.getProperty("chart.distortion", "10"))) == 0) {
                 maxTime = times;
                 Platform.runLater(() -> {
                 int upper = Math.max(10000, times);
                 hSlider.setMax(upper);
                 hScroll.setMax(maxTime - hSlider.getValue());

                 chartAddData(data);
                 int value = (int) (hSlider.getValue());
                 int lower = Math.max(0, times - value);
                 xAxis.setLowerBound(lower);
                 hScroll.setValue(lower);
                 xAxis.setUpperBound(upper);
                 xAxis.setTickUnit((upper - lower) / 10);
                 });
                 }*/
            }
        }

        @Override
        public void getPid(byte[] bytes, int[] data) {
            System.out.println(Arrays.toString(bytes));
            Platform.runLater(() -> {
                pidK.setText(String.valueOf(1.0d * data[2] / 10000));
                pidP.setText(String.valueOf(1.0d * data[3] / 10000));
                pidI.setText(String.valueOf(1.0d * data[4] / 10000));
                pidD.setText(String.valueOf(1.0d * data[5] / 10000));
                pidMax.setText(String.valueOf(data[6]));
                pidMin.setText(String.valueOf(data[7]));
            });
        }


        @Override
        public void sendData(byte[] data) {
            StringBuilder sb = new StringBuilder(sc.getProperty("msg.output", "output "));
            for (byte aByte : data) {
                String s = Integer.toHexString(Byte.toUnsignedInt(aByte));
                sb.append((s.length() == 1 ? "0" + s : s).toUpperCase() + " ");
            }
            Platform.runLater(() -> {
                ObservableList<String> items = msgView.getItems();
                items.add(sb.toString());
                msgView.scrollTo(items.size() - 1);
            });
        }
    };

    private void initConnView() {
        ObservableList<String> options = FXCollections.observableArrayList("TCP", "COM");
        interfaces.setItems(options);
        interfaces.getSelectionModel().selectFirst();

        ObservableList<String> optionsID = FXCollections.observableArrayList("01", "02");
        connID.setItems(optionsID);
        connID.getSelectionModel().selectFirst();

        connect.setOnMouseClicked(mouseEvent -> {
            String selectedItem = interfaces.getSelectionModel().getSelectedItem();
            String id = connID.getSelectionModel().getSelectedItem();
            try {
                if (isConnect.get()) {
                    nm.sendASCII("comm=0");
                    nm.close();
                    isConnect.set(false);
                    connect.setText("Connect");
                    clearLine();
                } else {
                    if (selectedItem.equals("TCP")) {
                        nm = new NetManage(sc.getProperty("conn.ip", "192.168.1.1"), Integer.parseInt(sc.getProperty("conn.port", "8899")), di);
                        isConnect.set(true);
                        nm.sendASCII("comm=1");
                        connect.setText("Disconnect");
                        clearLine();
                    } else if (selectedItem.equals("COM")) {
                        SerialPort[] commPorts = SerialPort.getCommPorts();

                        ChoiceDialog<String> dialog = new ChoiceDialog<>("", Arrays.stream(commPorts).map(n -> n.getSystemPortName()).collect(Collectors.toList()));
                        dialog.setTitle("Select Interface");
                        dialog.setHeaderText("Baudrate: "+sc.getProperty("com.baudrate", "460800"));
                        dialog.setContentText("Select Interface:");

                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()){
                            System.out.println("Your choice: " + result.get());
                        }

                        result.ifPresent(letter -> {
                            System.out.println("Your choice: " + letter);
                            if(!letter.equals("")) {
                                nm = new ComManage(SerialPort.getCommPort(letter), di, sc);
                                isConnect.set(true);
                                nm.sendASCII("comm=1");
                                connect.setText("Disconnect");
                                clearLine();
                            }
                        });
                    }
                }
            } catch (ConnectException e) {
                new Alert(Alert.AlertType.NONE, "Connection Failed!", new ButtonType[]{ButtonType.OK}).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void clearLine() {
        for (XYChart.Series<?, ?> datum : dataChart.getData())
            datum.getData().clear();
    }

    private int maxTime = 0;

    private void initLineView() {
        clearLine.setOnMouseClicked(mouseEvent -> clearLine());

        yAxis = new NumberAxis(-2048, 2048, 256);
        yAxis.setAutoRanging(false);
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return String.valueOf(number.longValue());
            }

            @Override
            public Number fromString(String s) {
                return null;
            }
        });

        xAxis = new NumberAxis(0, 10000, 1000);
        xAxis.setAutoRanging(false);
        xAxis.setForceZeroInRange(false);
        dataChart = new LineChart<>(xAxis, yAxis);
        dataChart.setAnimated(false);
        dataChart.setCreateSymbols(false);

        HBox.setHgrow(dataChart, Priority.ALWAYS);
        dataChart.setMaxHeight(Double.MAX_VALUE);
        dataChart.setMaxWidth(Double.MAX_VALUE);

        lineParent.getChildren().add(1, dataChart);

        pauseLine.setOnMouseClicked(mouseEvent -> {
            if (isPauseLine.get()) {
                isPauseLine.set(false);
                pauseLine.setText("Pause Chart");
            } else {
                isPauseLine.set(true);
                pauseLine.setText("Resume Recording");
            }

            chartX.setDisable(!isPauseLine.get());
            hScroll.setDisable(!isPauseLine.get());
        });

        vScroll.valueProperty().addListener((observableValue, number, t1) -> {
            chartY.setText(String.valueOf(t1.intValue()));
            chartVSyn();
        });

        double p = Math.pow(2, 11);
        vScroll.setMax(Integer.MAX_VALUE - p);
        vScroll.setMin(Integer.MIN_VALUE + p);
        vSlider.valueProperty().addListener((observableValue, number, t1) -> {
            double pow = Math.pow(2, t1.longValue());
            vScroll.setMax(Integer.MAX_VALUE - pow);
            vScroll.setMin(Integer.MIN_VALUE + pow);
            vScroll.setBlockIncrement(pow / 4);
            chartVSyn();
        });

        chartY.textProperty().addListener((observableValue, s, t1) -> {
            vScroll.setValue(Integer.parseInt(t1));
            chartVSyn();
        });

        chartX.textProperty().addListener((observableValue, s, t1) -> {
            hScroll.setValue(Integer.parseInt(t1));
            chartVSyn();
        });

        hScroll.valueProperty().addListener((observableValue, number, t1) -> {
            xAxis.setLowerBound(t1.longValue());
            xAxis.setUpperBound(hSlider.getValue() + t1.longValue());

            chartX.setText(String.valueOf(t1.longValue()));
        });

        hSlider.valueProperty().addListener((observableValue, number, t1) -> {
            hScroll.setMax(maxTime - hSlider.getValue());
            xAxis.setTickUnit(t1.longValue() / 10);

            xAxis.setUpperBound(hScroll.getValue() + t1.longValue());
        });
    }

    private void chartVSyn() {
        double slider = Math.pow(2, vSlider.getValue());
        double scroll = vScroll.getValue();

        double upper = scroll + slider;
        yAxis.setUpperBound(upper);
        double lower = scroll - slider;
        yAxis.setLowerBound(lower);
        yAxis.setTickUnit((upper - lower) / 10);
    }

    private List<CheckBoxTreeItem<String>> itemList;

    private String[] names = new String[]{sc.getProperty("ch.ch1", "CMD"),
            sc.getProperty("ch.ch2", "MODE"),
            sc.getProperty("ch.ch3", "SYS_VOL"),
            sc.getProperty("ch.ch4", "SYS_CUR"),
            sc.getProperty("ch.ch5", "CHG_VOL"),
            sc.getProperty("ch.ch6", "CHG_CUR"),
            sc.getProperty("ch.ch7", "MOT_VOL"),
            sc.getProperty("ch.ch8", "MOT_CUR"),
            sc.getProperty("ch.ch9", "MOT_SPEED"),
            sc.getProperty("ch.ch10", "HALL"),
            sc.getProperty("ch.ch11", "TEMP"),
            sc.getProperty("ch.ch12", "SET_SPEED"),
            sc.getProperty("ch.ch13", "NC"),
            sc.getProperty("ch.ch14", "status")};

    private void initTreeView() {

        CheckBoxTreeItem<String> channels = new CheckBoxTreeItem<>(sc.getProperty("ch", "Channels"));

        itemList = Arrays.stream(names).map(CheckBoxTreeItem::new).collect(Collectors.toList());

        channels.setExpanded(true);
        channels.getChildren().addAll(itemList);
        channel.setRoot(channels);
        channel.setCellFactory(CheckBoxTreeCell.forTreeView());

        for (int i = 0; i < itemList.size(); i++) {
            final int finalI = i;
            seriesList[finalI].setName(itemList.get(finalI).getValue());
            CheckBoxTreeItem<String> item = itemList.get(i);
            item.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
                if (newVal) {
                    dataChart.getData().add(seriesList[finalI]);
                } else {
                    itemList.get(finalI).setValue(names[finalI]);
                    dataChart.getData().remove(seriesList[finalI]);//line visible
                }
            });
        }

    }

}