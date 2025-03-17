package dao;

import models.ExchangeRate;

import java.math.BigDecimal;
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
        return null;
    }

    public ExchangeRate get(int baseCurrencyId, int targetCurrencyId) {
        ExchangeRate exchangeRate;
        CurrenciesDao currenciesDao = new CurrenciesDao();
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?;");
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            ResultSet resultSet = statement.executeQuery();
            exchangeRate = new ExchangeRate(resultSet.getInt("id"),
                    currenciesDao.get(resultSet.getInt("baseCurrencyId")),
                    currenciesDao.get(resultSet.getInt("targetCurrencyId")),
                    resultSet.getBigDecimal("rate"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }

    @Override
    public void add(ExchangeRate o) {

    }

    @Override
    public void update(ExchangeRate o, String... params) {

    }

    @Override
    public void delete(ExchangeRate o) {

    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        CurrenciesDao currencyDao = new CurrenciesDao();
        try (Connection connection = DatabaseUtil.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM ExchangeRates;");
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

    public Map<Integer, Integer> getAllIdPairs() {
        Map<Integer, Integer> idPairs = new HashMap<>();
        for (ExchangeRate exchangeRate : getAll()) {
            idPairs.put(exchangeRate.getBaseCurrency().getId(), exchangeRate.getTargetCurrency().getId());
        }
        return idPairs;
    }
}
