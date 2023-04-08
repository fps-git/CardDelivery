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

    public void clear(String input) {
        for (int i = 0; i < 8; i++) {
            $(input).sendKeys(Keys.BACK_SPACE);
        }
    }

    @Test
    public void shouldSuccess() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Ростов-на-Дону");
        clear("[data-test-id='date'] input");
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Петр Петров");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='notification']").should(appear, Duration.ofSeconds(15)).should(have(text("Успешно!")));
    }

    @Test
    public void shouldNotSuccessIfEarlyDate() {
        //Тут просто практикуюсь в Xpath, хоть и точный путь до элемента (с условием наличия input_invalid)
        // при проверке сообщения об ошибке в CSS будет намного короче

        open("http://localhost:9999/");
        $x("//*[@data-test-id='city']//input").setValue("Орёл");
        clear("[data-test-id='date'] input");
        $x("//*[@data-test-id='date']//input").setValue(dateCalculate(2));
        $x("//*[@data-test-id='name']//input").setValue("Иван Загоруйко");
        $x("//*[@data-test-id='phone']//input").setValue("+79031111111");
        $x("//*[@data-test-id='agreement']").click();
        $x("//button//*[text()='Забронировать']").click();
        $x("//*[@data-test-id='date']//span[contains(@class, 'input_invalid')]//*[@class='input__sub']").should(appear).shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    @Test
    public void shouldNotSuccessWithoutCheckbox() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Майкоп");
        clear("[data-test-id='date'] input");
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Василий Иванов");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $(withText("Забронировать")).click();
        $("[data-test-id='agreement'].input_invalid").should(appear);
    }

    @Test
    public void shouldNotSuccessWithoutCity() {
        open("http://localhost:9999/");
        clear("[data-test-id='date'] input");
        $("[data-test-id='date'] input").setValue(dateCalculate(3));
        $("[data-test-id='name'] input").setValue("Сергей Безгородов");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='city'].input_invalid").should(appear).shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldNotSuccessWithoutDate() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        clear("[data-test-id='date'] input");
        $("[data-test-id='name'] input").setValue("Виталий Безденный");
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='date'] .input_invalid .input__sub").should(appear).shouldHave(text("Неверно введена дата"));
    }

    @Test
    public void shouldNotSuccessWithoutName() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        clear("[data-test-id='date'] input");
        $("[data-test-id='date'] input").setValue(dateCalculate(30));
        $("[data-test-id='phone'] input").setValue("+79031234567");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub").should(appear).shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldNotSuccessWithoutPhone() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Биробиджан");
        clear("[data-test-id='date'] input");
        $("[data-test-id='date'] input").setValue(dateCalculate(100));
        $("[data-test-id='name'] input").setValue("Аркадий Безномеров");
        $("[data-test-id='agreement']").click();
        $(withText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").should(appear).shouldHave(text("Поле обязательно для заполнения"));
    }
}