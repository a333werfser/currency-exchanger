package dao;

import models.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao implements Dao<Currency> {
    @Override
    public Currency get(int id) {
        Currency currency;
        try (Connection connection = DatabaseUtil.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery(
                     "SELECT * FROM Currencies WHERE id = " + id + ";")) {
            currency = new Currency(resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("fullname"),
                    resultSet.getString("sign"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }

    public Currency get(String code) {
        Currency currency;
        try (Connection connection = DatabaseUtil.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery(
                     "SELECT * FROM Currencies WHERE code = \'" + code + "\';")) {
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
        try (Connection connection = DatabaseUtil.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Currencies;")) {
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

    public List<String> getAllCodes() {
        List<String> currenciesCodes = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Currencies;")) {
            while (resultSet.next()) {
                currenciesCodes.add(resultSet.getString("code"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currenciesCodes;
    }
}
