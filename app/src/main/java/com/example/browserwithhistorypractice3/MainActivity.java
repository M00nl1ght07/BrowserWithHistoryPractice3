package com.example.browserwithhistorypractice3;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WebView webView; // объявление webView компонента. Компонент на движке WebKit

    private BrowserHistory browserHistory; // объявляем private класс для хранения истории

    private class MyWebClient extends WebViewClient { // создаем private класс MyWebClient, наследующийся от WebViewClient
        @Override // переопределение метода из родительского класса
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) { // переопределяем метод shouldOverrideUrlLoading из род. класса
            String url = request.getUrl().toString(); // в переменную url указываем URL из WebResourceRequest
            browserHistory.addUrl(url); // добавление в историю url посещенной страницы
            view.loadUrl(url); // загружаем URL из WebResourceRequest
            return false; // клиент обарабывает загрузку самостоятельно (false для перехода по ссылкам)
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView); // поиск webView элемента по id, объявление переменной

        webView.getSettings().setJavaScriptEnabled(true); // включение поддержки js

        webView.loadUrl("https://ya.ru"); // указание страницы загрузки

        webView.setWebViewClient(new MyWebClient()); // определяем экземпляр класса MyWebClient

        browserHistory = new BrowserHistory(); // опеределяем объект типа browserHistory

        OnBackPressedCallback callback = new OnBackPressedCallback(true) { // создание объекта класса для создания обратных вызовов при обработке нажатия кнопки назад
            @Override
            public void handleOnBackPressed() { // переопределенный метод для возвращения назад
                if (webView.canGoBack()) { // если переход назад возможен
                    webView.goBack(); // осуществление перехода
                } else {
                    finish(); // Если нельзя перейти назад, вызываем finish(), активити закрывается
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback); // привязка созданного обратного вызова к диспетчеру нажатия. Добавление обратного вызова каллбек
        // для возвращения на предыдущий элемент

        Button historyButton = findViewById(R.id.buttonHistory); // объявление и иницализация кнопки для просмотра истории

        historyButton.setOnClickListener(new View.OnClickListener() { // слушатель нажатий на кнопку
            @Override
            public void onClick(View view) { // метод onClick
                showHistoryDialog(); // метод открытия диалогового окна
            }
        });
    }
    private void showHistoryDialog() { // метод диалогового окна с историей
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // создание экземпляра AlertDialog
        builder.setTitle("History"); // Установка названия окна

        List<String> historyList = browserHistory.getHistoryList(); // получение в список всех url адресов
        String[] historyArray = historyList.toArray(new String[0]); // преобразование в массив строк url адресов

        builder.setItems(historyArray, new DialogInterface.OnClickListener() { // установка в окно списка эоементов из массива строк
            @Override
            public void onClick(DialogInterface dialog, int which) { // слушатель по нажатию на url
                String selectedUrl = historyArray[which]; // получение текущего url выбранного
                webView.loadUrl(selectedUrl); // загрузка url адреса
            }
        });

        builder.show(); // метод отображения на экране диалогового окна
    }

    public class BrowserHistory { // класс для управления историей
        private List<String> historyList; // объявление списка для хранения адресов

        public BrowserHistory() { // объект BrowserHistory
            historyList = new ArrayList<>(); // инициализация списка для url хранения
        }

        public void addUrl(String url) { // метод добавления url в список
            historyList.add(url);
        }

        public String getLastUrl() { // метод возврата последнего url адреса в историю
            if (!historyList.isEmpty()) { // если не пустой список
                return historyList.get(historyList.size() - 1); // возврат последнего url посещенного
            }
            return null; // null если пустая строка
        }

        public List<String> getHistoryList() { // метод для возврата всех url посещенных
            return historyList;
        }
    }
}