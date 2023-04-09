import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    public String dateCalculate(int daysFromToday) {
        LocalDate date = LocalDate.now().plusDays(daysFromToday);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    @Test
    public void shouldSuccess() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Ростов-на-Дону");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(5));
        $("[data-test-id='name'] input").setValue("Петр Петров");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='notification'] .notification__content").should(appear, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно забронирована на " + dateCalculate(5)));
    }

    @Test
    public void shouldNotSuccessIfEarlyDate() {
        //Тут просто практикуюсь в Xpath, хоть и точный путь до элемента (с условием наличия input_invalid)
        //при проверке сообщения об ошибке в CSS будет намного короче

        open("http://localhost:9999/");
        $x("//*[@data-test-id='city']//input").setValue("Орёл");
        $x("//*[@data-test-id='date']//input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//*[@data-test-id='date']//input").setValue(dateCalculate(2));
        $x("//*[@data-test-id='name']//input").setValue("Иван Загоруйко");
        $x("//*[@data-test-id='phone']//input").setValue("+79031111111");
        $x("//*[@data-test-id='agreement']").click();
        $x("//button//*[text()='Забронировать']").click();
        $x("//*[@data-test-id='date']//span[contains(@class, 'input_invalid')]//*[@class='input__sub']")
                .should(appear).shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    @Test
    public void shouldNotSuccessWithoutCheckbox() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Майкоп");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Василий Иванов");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $(withText("Забронировать")).click();
        $("[data-test-id='agreement'].input_invalid").should(appear);
    }

    @Test
    public void shouldNotSuccessIfEnglishName() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Johnny English");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $(withText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid").should(appear)
                .shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void shouldNotSuccessWithoutCity() {
        open("http://localhost:9999/");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Сергей Безгородов");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='city'].input_invalid").should(appear)
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldNotSuccessWithoutDate() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='name'] input").setValue("Виталий Безденный");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='date'] .input_invalid .input__sub").should(appear)
                .shouldHave(text("Неверно введена дата"));
    }

    @Test
    public void shouldNotSuccessWithoutName() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(30));
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub").should(appear)
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldNotSuccessWithoutPhone() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Биробиджан");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(100));
        $("[data-test-id='name'] input").setValue("Аркадий Безномеров");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").should(appear)
                .shouldHave(text("Поле обязательно для заполнения"));
    }


    // -------------------- ДОМАШНЕЕ ЗАДАНИЕ №2 -----------------------

    @Test
    public void shouldChooseCityFromList() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Ка");
        $(withText("Казань")).click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Артур Казанский");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='notification'] .notification__content").should(appear, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно забронирована на " + dateCalculate(3)));
    }

    @Test
    public void shouldChooseDateFromCalendar() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Курск");
        $("[data-test-id='date'] input").click();

        // Блок установки дня из календаря, в поле daysPlus ввести через какое количество дней от текущего
        // нужно забронировать доставку карты
        int daysPlus = 7;
        //----------------  Код блока расчета  ------------------------------
        int remainingDays = $$("[data-day]").size();
        if (remainingDays <= (daysPlus - 3)) {
            $("[data-step='1']").click();
            $$("[data-day]").get(daysPlus - 3 - remainingDays).click();
        } else {
            $$("[data-day]").get(daysPlus - 3).click();
        }
        //---------------- Конец блока выбора дня из календаря ---------------

        $("[data-test-id='name'] input").setValue("Филипп Матвеев");
        $("[data-test-id='phone'] input").setValue("+11111111111");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='notification'] .notification__content").should(appear, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно забронирована на " + dateCalculate(daysPlus)));
    }
}