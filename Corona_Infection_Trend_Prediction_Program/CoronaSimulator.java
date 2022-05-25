package corona;

//ファイル操作用
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
//配列ソート用
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

//グラフ描画用
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

class Prefecture {
	int[] kensasu = new int[365];
	int[] yoseisha = new int[365];
	int[] shibosha = new int[365];


	//引数なしコンストラクタ（初期値で奈良を代入）
	Prefecture() {
		resetData();
		try {
			setData("Nara.txt");
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	//引数ありコンストラクタ
	Prefecture(String fileLocation) {
		resetData();
		try {
			setData(fileLocation);
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	//配列を初期化するために、すべてを0で埋める。
	void resetData() {
		for (int i = 0; i < 365; i++) {
			kensasu[i] = 0;
			yoseisha[i] = 0;
			shibosha[i] = 0;
		}
	}

	//ファイルの場所を引数で指定し、その中のデータを配列に格納
	void setData(String fileLocation) throws Exception{
		//ファイル読み込み用の設定
		String line;
		int index = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fileLocation));

		//日付計算用の設定
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

		while((line = reader.readLine()) != null) {
			//一行ずつ読み取り、配列dataに格納
			String[] data = line.split("\t");

			//日付
			Date date = format.parse(data[0]);

			//2020/1/1からの経過日数を計算（例えば、2020/1/3なら、day = 4となるように設定。
			long day = (date.getTime() - format.parse("2020/1/1").getTime()) / (1000 * 60 * 60 * 24);

			kensasu[(int)day] = Integer.parseInt(data[1]);
			yoseisha[(int)day] = Integer.parseInt(data[2]);
			shibosha[(int)day] = Integer.parseInt(data[3]);

			//System.out.println("data[0]=" + data[0] + " date=" + date + " day=" + day + " kensasu=" + kensasu[(int)day] + " yoseisya=" + yoseisya[(int)day] + " shibosha=" + shibosha[(int)day]);

		}
		reader.close();
	}
}

class Kinki extends Prefecture{
	String name;

	Kinki(String name, String fileLocation) {
		resetData();
		this.name = name;
		try {
			setData(fileLocation);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}

class BasicData {
	String name;
	Kinki data;

	//引数ありコンストラクタ
	BasicData(Kinki data) {
		setData(data);
	}

	void setData(Kinki data) {
		this.data = data;
	}

	//平均はデータが0の時を除くように設定。
	float calculateAverage(int[] array) {
		int sum = 0;
		float counter = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0) {
				sum += array[i];
				counter++;
			}
		}
		return sum / counter;
	}

	int calculateMaximum(int[] array) {
		Arrays.sort(array);
		return array[364];
	}

	//最小値は0を除くように設定。
	int calculateMinimum(int[] array) {
		Arrays.sort(array);
		int tmp = 0;
		while(array[tmp] == 0) {
			tmp++;
			if (tmp >= array.length) {
				tmp = 0;
				break;
			}
		}
		return array[tmp];
	}

	//中央値を計算・0を除くように設定。
	float calculateMedian(int[] array) {
		Arrays.sort(array);
		int counter = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0) {
				counter = i;
				break;
			}
		}

		//中央値が１つの時
		if ((365 - counter) % 2 == 1) {
			return array[(365 - counter + 1) / 2 + counter - 1];
		}else {
			//中央値が２つの時
			return (array[counter - 1 + (365 - counter) / 2] + array[counter + (365 - counter) / 2]) / 2;
		}
	}

	public String toString() {
		return (
				"\n都道府県:" + data.name +
				"\n 検査数" +
				"\n 平均：" + calculateAverage(data.kensasu) +
				"\t 最大値：" + calculateMaximum(data.kensasu) +
				"\t 最小値：" + calculateMinimum(data.kensasu) +
				"\t 中央値：" + calculateMedian(data.kensasu) +
				"\n 陽性者" +
				"\n 平均：" + calculateAverage(data.yoseisha) +
				"\t 最大値：" + calculateMaximum(data.yoseisha) +
				"\t 最小値：" + calculateMinimum(data.yoseisha) +
				"\t 中央値：" + calculateMedian(data.yoseisha) +
				"\n 死亡者" +
				"\n 平均：" + calculateAverage(data.shibosha) +
				"\t 最大値：" + calculateMaximum(data.shibosha) +
				"\t 最小値：" + calculateMinimum(data.shibosha) +
				"\t 中央値：" + calculateMedian(data.shibosha)
				);
	}
}

class Simulator {
	int population;
	Kinki place;

	float[] forecastYoseisha = new float[365];
	float[] Jogaishasu = new float[365];
	float[] Mikansenshasu = new float[365];

	float Kansenritsu = 0.0002f;
	float Jogairitsu = 0.14f;

	Simulator() {
		population = 0;
	}

	Simulator(int population, Kinki place) {
		this.population = population;
		this.place = place;
	}

	void simulator() {
		forecastYoseisha[0] = 1;
		Mikansenshasu[0] = population - forecastYoseisha[0];
		Jogaishasu[0] = 0;

		float[] xs0 = new float[364];
		float[] xs1 = new float[364];
		float[] xs2 = new float[364];
		float[] xs3 = new float[364];

		float[] ys0 = new float[364];
		float[] ys1 = new float[364];
		float[] ys2 = new float[364];
		float[] ys3 = new float[364];

		float[] zs0 = new float[364];
		float[] zs1 = new float[364];
		float[] zs2 = new float[364];
		float[] zs3 = new float[364];

		for (int i = 0; i < 364; i++) {
			xs0[i] = -Kansenritsu * Mikansenshasu[i] * forecastYoseisha[i];
			ys0[i] = Kansenritsu * (Mikansenshasu[i] * forecastYoseisha[i] - Jogairitsu * forecastYoseisha[i]);
			zs0[i] = forecastYoseisha[i] * Jogairitsu;

			xs1[i] = -Kansenritsu * (Mikansenshasu[i] + xs0[i]/2) * (forecastYoseisha[i] + ys0[i]/2);
			ys1[i] = (Kansenritsu * (Mikansenshasu[i] + xs0[i]/2) * (forecastYoseisha[i] + ys0[i]/2) - Jogairitsu * (forecastYoseisha[i] + ys0[i]/2));
			zs1[i] = Jogairitsu * (forecastYoseisha[i] + ys0[i]/2);

			xs2[i] = -Kansenritsu * (Mikansenshasu[i] + xs1[i]/2) * (forecastYoseisha[i] + ys1[i]/2);
			ys2[i] = (Kansenritsu * (Mikansenshasu[i] + xs1[i]/2) * (forecastYoseisha[i] + ys1[i]/2) - Jogairitsu * (forecastYoseisha[i] + ys1[i]/2));
			zs2[i] = Jogairitsu * (forecastYoseisha[i] + ys1[i]/2);


			xs3[i] = -Kansenritsu * (Mikansenshasu[i] + xs2[i]) * (forecastYoseisha[i] + ys2[i]);
			ys3[i] = Kansenritsu * (Mikansenshasu[i] + xs2[i]) * (forecastYoseisha[i] + ys2[i]) - Jogairitsu * (forecastYoseisha[i] + ys2[i]);
			zs3[i] = Jogairitsu * (forecastYoseisha[i] + ys2[i]);

			Mikansenshasu[i + 1] = Mikansenshasu[i] + (xs0[i] + xs1[i] * 2 + xs2[i] * 2 + xs3[i])/6;
			forecastYoseisha[i + 1] = forecastYoseisha[i] + (ys0[i] + ys1[i] * 2 + ys2[i] * 2 + ys3[i])/6;
			Jogaishasu[i + 1] = Jogaishasu[i] + (zs0[i] + zs1[i] * 2 + zs2[i] * 2 + zs3[i])/6;
		}
	}

	void accurateCaliculater() {
		long minimum = 0;
		int minimumKansenritsu = 0;
		for (int i = 0; i < population; i += 10) {
			Kansenritsu = (float)i / population;
			simulator();
			long sum = 0;

			for (int j = 0; j < place.yoseisha.length - 91; j++) {
				sum += (place.yoseisha[j + 91] - forecastYoseisha[j]) * (place.yoseisha[j + 91] - forecastYoseisha[j]);
			}

			if (i % 1000 == 0) {
				//System.out.println(sum);
			}

			if (i == 0) {
				minimum = sum;
				minimumKansenritsu = 0;
			}else {
				if (sum < minimum) {
					minimum = sum;
					minimumKansenritsu = i;
				}
			}
		}

		Kansenritsu = (float)minimumKansenritsu / population;
		simulator();
	}
}

class DrawGraph {
	//Kinki place;
	DrawGraph() {

	}

	DrawGraph(Kinki place) {

	}

	void drawBarGraph() {
		//フィールドの情報を表示
	}
}

class DrawComparedGraph extends DrawGraph{
	DrawComparedGraph() {

	}

	DrawComparedGraph(Kinki place1, Kinki place2) {

	}
}

public class CoronaSimulator extends Application {

	@Override public void start(Stage stage) {

		stage.setTitle("Corona Simulation");

        //軸を定義
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("1月1日からの日数");
        yAxis.setLabel("人数");
        //チャートの作成
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle(GraphNamePrefecture);

        //シリーズを定義
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("検査数");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("陽性者数");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("死亡者数");

        //シリーズにデータを入力
        for(int day=0; day<365; day++ ) {
            series1.getData().add(new XYChart.Data(day, GraphKensasu[day]));
            series2.getData().add(new XYChart.Data(day, GraphYoseisha[day]));
            series3.getData().add(new XYChart.Data(day, GraphShibosha[day]));
        }

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series1, series2, series3);

        stage.setScene(scene);
        stage.show();
    }

	public static String GraphNamePrefecture="Asahikawa";
	public static int[] GraphKensasu=new int[365];
	public static int[] GraphYoseisha=new int[365];
	public static int[] GraphShibosha=new int[365];

	public static void main(String[] args) {

		//県の数
		int numberOfPrefecture = 6;

		//データの入力
		Kinki[] data = new Kinki[numberOfPrefecture];
		data[0] = new Kinki("Nara", "Nara.txt");
		data[1] = new Kinki("Hyogo", "Hyogo.txt");
		data[2] = new Kinki("Osaka", "Osaka.txt");
		data[3] = new Kinki("Mie", "Mie.txt");
		data[4] = new Kinki("Wakayama", "Wakayama.txt");
		data[5] = new Kinki("Kyoto", "Kyoto.txt");

		//データの特長を表す数値を計算する
		BasicData[] basicData = new BasicData[numberOfPrefecture];
		for (int i = 0; i < numberOfPrefecture; i++) {
			basicData[i] = new BasicData(data[i]);
		}

		//データを表す特徴量の表示
		for (int i = 0; i < numberOfPrefecture; i++) {
			System.out.println(basicData[i]);
		}

		//データの予測
		//シミュレーションするにあたって、人口をここで手動で入力。
		Simulator naraSim = new Simulator(1365008, data[0]);
		Simulator mieSim = new Simulator(1815827, data[1]);

		//accurateCaliculater()で、感染率をルンゲクッタのシミュレーションにより計算
		naraSim.accurateCaliculater();
		System.out.println(naraSim.Kansenritsu);

		mieSim.accurateCaliculater();
		System.out.println(mieSim.Kansenritsu);

		//ユーザの入力によって表示する内容を変える
		System.out.println("奈良を0，兵庫を1,大阪を2,三重を3,和歌山を4,京都を5とする。グラフを見たい府県の番号を入力。");

		//例外なアルファベットが入力された場合は例外処理をする。
		try {
			Scanner stdIn  = new Scanner(System.in);
			String numPrefString = stdIn.next();
			int numPref = Integer.parseInt(numPrefString);
			System.out.println("入力された数字:" + numPref);

			//また、例外な数字が入力された場合は例外処理する
			try {
				//ファイルに出力
				GraphNamePrefecture=data[numPref].name;
				for( int day=0; day<365; day++ ) {
					GraphKensasu[day]=data[numPref].kensasu[day];
					GraphYoseisha[day]=data[numPref].yoseisha[day];
					GraphShibosha[day]=data[numPref].shibosha[day];
				}

				launch(args);

			}catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("0から5の数字を入力してください。");
				System.out.println("例外は" + e + "です。");
			}

		}catch (NumberFormatException e) {
			System.out.println("入力されたのは数字ではありません。");
			System.out.println("例外は" + e + "です。");
		}

	}

}


