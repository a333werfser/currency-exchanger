package dao;

import models.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesDao implements Dao<ExchangeRate> {

    @Override
    public ExchangeRate get(int id) {
        CurrenciesDao currenciesDao = new CurrenciesDao();
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM ExchangeRates WHERE Id = ?;");
            ResultSet resultSet = statement.executeQuery();
            return new ExchangeRate(resultSet.getInt("id"),
                    currenciesDao.get(resultSet.getInt("baseCurrencyId")),
                    currenciesDao.get(resultSet.getInt("targetCurrencyId")),
                    resultSet.getBigDecimal("rate"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeRate get(int baseCurrencyId, int targetCurrencyId) {
        CurrenciesDao currenciesDao = new CurrenciesDao();
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?;");
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            ResultSet resultSet = statement.executeQuery();
            return new ExchangeRate(resultSet.getInt("id"),
                    currenciesDao.get(resultSet.getInt("baseCurrencyId")),
                    currenciesDao.get(resultSet.getInt("targetCurrencyId")),
                    resultSet.getBigDecimal("rate"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(ExchangeRate o) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO ExchangeRates " +
                            "(baseCurrencyId, targetCurrencyId, rate)" +
                            "VALUES (?, ?, ?);");
            statement.setInt(1, o.getBaseCurrency().getId());
            statement.setInt(2, o.getTargetCurrency().getId());
            statement.setBigDecimal(3, o.getRate());
            statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExchangeRate o, String... params) {

    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        CurrenciesDao currencyDao = new CurrenciesDao();
        try (Connection connection = DatabaseUtil.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT * FROM ExchangeRates;");
            while (resultSet.next()) {
                exchangeRates.add(new ExchangeRate(resultSet.getInt("id"),
                        currencyDao.get(resultSet.getInt("baseCurrencyId")),
                        currencyDao.get(resultSet.getInt("targetCurrencyId")),
                        resultSet.getBigDecimal("rate")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }
}
