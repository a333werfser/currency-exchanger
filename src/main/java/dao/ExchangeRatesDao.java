package dao;

import models.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDao implements Dao<ExchangeRate> {

    @Override
    public ExchangeRate get(int id) {
        return null;
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
    public List getAll() {
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
}
