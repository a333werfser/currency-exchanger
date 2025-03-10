package dao;

import models.Currency;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<Currency> {
    @Override
    public Currency get(int id) {
        Currency currency;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:exchanger-database.db");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Currencies WHERE id = "
                     + id + ";")) {
            currency = new Currency(resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("fullname"),
                    resultSet.getString("sign"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }

    @Override
    public void add(Currency o) {

    }

    @Override
    public void update(Currency o, String... params) {

    }

    @Override
    public void delete(Currency o) {

    }

    @Override
    public List<Currency> getAll() {
        List<Currency> currencies = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Code\\Pets\\idea-related\\currency-exchanger\\exchanger-database");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Currencies;")) {
            while (resultSet.next()) {
                currencies.add(new Currency(resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }
}
