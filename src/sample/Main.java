package sample;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    private Group group;
    private Scene scene;



    String fromJson;
    String toJson;
    Gson gson = new Gson();
    ArrayList<Result> results = new ArrayList<Result>();


    Label labelCountClick = new Label("Кол-во ходов: 0");
    private String nickName; //ник игрока
    Label labelNick = new Label();
    Label labelCountWin = new Label();
    private int countWinner=0;
    int idd=0; //id игрока
    Label labelResult = new Label ("");
    private int countClick = 0;
    private TextField textField = new TextField("Ваш ник");
    private  Button btlogin = new Button("Войти");
    private  Button btresult = new Button("Результаты");
    private Button[] btns = new Button[16];
    private ArrayList<Integer> rndarr = GenerateNumbers();
    private int freex = 0;
    private int freey = 0;
    private int freeid = 0;
    private int oldx = 0;
    private int oldy = 0;
    private int oldid = 0;
    Button btnundo = new Button("Отменить");

    private static int btnsize = 50;


    //проверка на правильность
    private int CheckResult()
    {
        int res = 1;
        for (int i=0;i<14;i++)
        {
            if (rndarr.get(i) !=i) res = 0;
        }
        return res;
    }
    //правильный порядок чисел
    private ArrayList<Integer> GenerateNumbers()
    {
        ArrayList<Integer> result = new ArrayList<>();
        do
        {
            ArrayList<Integer> res = new ArrayList<>();
                for (int i = 0; i < 16; i++)
            {
                Random random = new Random();
                int r = random.nextInt(16);
                if (!res.contains(r))
                {
                    res.add(r);
                }
                else
                {
                    i--;
                }
            }
              result =res;
        }
        while (!canBeSolved(result));

        return result;
    }
    @Override
    public void start(Stage primaryStage) throws Exception{


        //десерелизация
        try {
            File file = new File("results.txt");
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);

            // считаем сначала первую строку

            fromJson = reader.readLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type itemsListType = new TypeToken<ArrayList<Result>>() {}.getType();
        results = new Gson().fromJson(fromJson, itemsListType);
        //конец десерелизации

        initBtnsArray(); //создание кнопок 1-16
        Group grlogin = new Group();
        group = new Group();
        group.getChildren().add(getGrid());

        btnundo.setDisable(true);
        textField.setPrefColumnCount(11);
        btlogin.setLayoutX(40);
        btlogin.setLayoutY(40);

        //нажатие на кнопку войти
        btlogin.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                nickName = textField.getText();
                labelNick.setText("Ваш ник: "+ nickName);
                int i=0;
                //есть ли такой ник? если есть получаем его id (idd)
                for (Result item : results){

                    if(item.getNickname().equals(nickName)){
                        idd = i;
                        countWinner=item.getCount();
                        labelCountWin.setText("Побед: "+countWinner);
                        break;
                    }
                    i++;

                }

                //если нет такого имени то создается пользователь
                if (idd == 0) {
                    results.add(new Result(nickName,0));
                    idd=results.size()-1;
                }

                primaryStage.setScene(scene);
                primaryStage.show();
            }
        });
        //описание кнопок (параметры)
        grlogin.getChildren().add(textField);
        grlogin.getChildren().add(btlogin);
        Button btnstart = new Button("Начать сначала");
        btnstart.setLayoutX(50);
        btnstart.setLayoutY(210);
        btnundo.setLayoutX(65);
        btnundo.setLayoutY(240);
        labelCountClick.setLayoutX(2);
        labelCountClick.setLayoutY(290);
        labelNick.setLayoutX(2);
        labelNick.setLayoutY(270);
        labelCountWin.setText("Побед: "+countWinner);
        labelCountWin.setLayoutX(2);
        labelCountWin.setLayoutY(310);
        btresult.setLayoutX(65);
        btresult.setLayoutY(350);
        labelResult.setLayoutX(2);

        // при закрытии окна - серелизация
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                if(results.get(idd).getCount()==0){
                    results.remove(idd);
                }
              
                String strToJson = new Gson().toJson(results);
                try(FileWriter writer = new FileWriter("results.txt"))
                {
                    // запись всей строки
                    writer.write(strToJson);

                    writer.flush();
                }
                catch(IOException ex){

                    System.out.println(ex.getMessage());
                }
            }
        });
        // показать результаты через Alert
        btresult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Результаты");
                alert.setHeaderText(null);
                String strres="Ник \\ Побед\n";
                for (Result item : results){
                    strres+=item.getNickname()+" : "+item.getCount().toString()+"\n";
                }
                alert.setContentText(strres);
                alert.showAndWait();
            }
        });
        //кнопка отмены
        btnundo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Button b = (Button)(event.getSource());
                int x = oldid % 4;
                int y = oldid / 4;


                    btns[oldid].setText("");
                    btns[freeid].setText((rndarr.get(oldid)+1)+"");

                    int tmp = rndarr.get(freeid);
                    rndarr.set(freeid,rndarr.get(oldid));
                    rndarr.set(oldid,tmp);

                    freex=x;
                    freey=y;
                    freeid=oldid;
                    countClick++;
                    btnundo.setDisable(true);
                    labelCountClick.setText("Кол-во ходов: "+countClick);
            }
        });
        // кнопка рестарта
        btnstart.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                rndarr = GenerateNumbers();
                for(int i = 0; i < btns.length; i++)
                {
                    btns[i].setText((rndarr.get(i) + 1) + "");
                    if (rndarr.get(i) == 15) {
                        btns[i].setText("");
                        freex = i % 4;
                        freey = i / 4;
                        freeid = i;
                    }
                }
                countClick=0;
                labelCountClick.setText("Кол-во ходов: "+countClick);
            }
        });
        group.getChildren().add(labelNick);
        group.getChildren().add(btnstart);
        group.getChildren().add(btnundo);
        group.getChildren().add(labelCountClick);
        group.getChildren().add(labelCountWin);
        group.getChildren().add(btresult);
        scene = new Scene(group);
        Scene sclogin = new Scene(grlogin);
        primaryStage.setTitle("Пятнашки");
        primaryStage.setScene(sclogin);
        primaryStage.show();
    }

    //функция возвращающая таблицу кнопок
    private Pane getGrid() {
        int i = 0;
        GridPane gridPane = new GridPane();
        for(Button b : btns) {
            int x = i % 4;
            int y = i / 4;
            gridPane.add(b, x*btnsize, y*btnsize);
            i++;
        }
        return gridPane;
    }
        //проверка на решаемость головоломки
    private boolean canBeSolved(ArrayList<Integer> invariants) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (invariants.get(i) == 0) {
                sum += i / 4;
                continue;
            }

            for (int j = i + 1; j < 16; j++) {
                if (invariants.get(j) < invariants.get(i))
                    sum ++;
            }
        }
        System.out.println(sum % 2 == 0);
        return sum % 2 == 0;
    }
    //функция инициализации кнопок
    private void initBtnsArray() {
        for(int i = 0; i < btns.length; i++) {
            btns[i] = new Button((rndarr.get(i) + 1)+"");
            btns[i].setMaxWidth(btnsize);
            btns[i].setMaxHeight(btnsize);
            btns[i].setMinWidth(btnsize);
            btns[i].setMinHeight(btnsize);
            if (rndarr.get(i)==15)
            {
                btns[i].setText("");
                freex = i % 4;
                freey = i / 4;
                freeid = i;
            }
            btns[i].setId(i+"");
            //лбработка нажатия на для смены кнопок
            btns[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Button b = (Button)(event.getSource());
                    int id = Integer.parseInt(b.getId());
                    int x = id % 4;
                    int y = id / 4;

                    if (Math.abs(freex-x)+Math.abs(freey-y)==1)
                    {
                        //тогда мы меняем местами пустышку и текущую клетку
                        oldid = freeid;
                        oldx = freex;
                        oldy = freey;
                        btns[id].setText("");
                        btns[freeid].setText((rndarr.get(id)+1)+"");

                        int tmp = rndarr.get(freeid);
                        rndarr.set(freeid,rndarr.get(id));
                        rndarr.set(id,tmp);

                        freex=x;
                        freey=y;
                        freeid=id;

                        btnundo.setDisable(false);
                        countClick++;
                        labelCountClick.setText("Кол-во ходов: "+countClick);
                    }
                    //проверка решил ли? и вывод инфо
                    if (CheckResult()==1) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Пятнашки");
                        alert.setHeaderText(null);
                        alert.setContentText("Вы выиграли!");
                        alert.showAndWait();
                        countWinner++;
                        results.get(results.size()-1).setCount(results.get(results.size()-1).getCount()+1);
                        labelCountWin.setText("Побед: "+countWinner);
                    }
                }

            });




        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}