package dao;

import models.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDao implements Dao<Currency> {
    @Override
    public Currency get(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery(
                     "SELECT * FROM Currencies WHERE id = " + id + ";")) {
            return new Currency(resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("fullname"),
                    resultSet.getString("sign"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency get(String code) {
        Currency currency;
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM Currencies WHERE code = ?");
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
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
    public void add(Currency currency) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Currencies (code, fullname, sign) VALUES (?, ?, ?)");
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Currency currency, String field) {

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
