package dao;

import models.Currency;
import models.ExchangeRate;
import util.ServletUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public void update(ExchangeRate o, String rate) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE ExchangeRates SET Rate = ? WHERE BaseCurrencyId = ? " +
                            "AND TargetCurrencyId = ?;");
            statement.setBigDecimal(1, new BigDecimal(rate));
            statement.setInt(2, o.getBaseCurrency().getId());
            statement.setInt(3, o.getTargetCurrency().getId());
            statement.executeUpdate();
        }
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

    public ExchangeRate[] getSimilarExchangeRates(String code1, String code2) {
        if (ServletUtil.codesNotExist(code1, code2)) {
            return null;
        }

        ExchangeRate[] similarExchangeRates = new ExchangeRate[2];
        List<ExchangeRate> filteredExchangeRate = new ArrayList<>();

        for (ExchangeRate exchangeRate : new ExchangeRatesDao().getAll()) {
            if (exchangeRate.getTargetCurrency().getCode().equals(code1) ||
                    exchangeRate.getTargetCurrency().getCode().equals(code2)) {
                filteredExchangeRate.add(exchangeRate);
            }
        }

        for (int i = 0; i < filteredExchangeRate.size(); i++) {
            Currency baseCurrency = filteredExchangeRate.get(i).getBaseCurrency();

            for (int j = 0; j < filteredExchangeRate.size(); j++) {
                if (i != j) {
                    int index = 0;
                    Currency baseCurrency2 = filteredExchangeRate.get(j).getBaseCurrency();
                    if (baseCurrency.getCode().equals(baseCurrency2.getCode())) {
                        similarExchangeRates[index++] = filteredExchangeRate.get(i);
                        similarExchangeRates[index] = filteredExchangeRate.get(j);
                    }
                }
            }
        }

        if (similarExchangeRates[0] == null || similarExchangeRates[1] == null) {
            return null;
        }
        else {
            return similarExchangeRates;
        }
    }
}
