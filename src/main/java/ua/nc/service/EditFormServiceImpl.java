package ua.nc.service;

import org.apache.log4j.Logger;
import ua.nc.dao.FieldDAO;
import ua.nc.dao.ListTypeDAO;
import ua.nc.dao.ListValueDAO;
import ua.nc.dao.enums.DataBaseType;
import ua.nc.dao.exception.DAOException;
import ua.nc.dao.factory.DAOFactory;
import ua.nc.entity.profile.Field;
import ua.nc.entity.profile.ListValue;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Neltarion on 12.05.2016.
 */
public class EditFormServiceImpl implements EditFormService{

    private final static Logger LOGGER = Logger.getLogger(EditFormServiceImpl.class);
    private final static DAOFactory daoFactory  = DAOFactory.getDAOFactory(DataBaseType.POSTGRESQL);

    @Override
    public List<Field> getAllFields(Integer ces_id) {
        Connection connection = daoFactory.getConnection();
        FieldDAO fieldDAO = daoFactory.getFieldDAO(connection);
        List<Field> fields = new LinkedList<>();
        try {
            fields.addAll(fieldDAO.getFieldsForCES(ces_id));
        } catch (DAOException e) {
            LOGGER.error(e);
        } finally {
            daoFactory.putConnection(connection);
        }
        return fields;
    }

    @Override
    public List<ListValue> getListValues(Integer listId) {
        Connection connection = daoFactory.getConnection();
        ListValueDAO listValueDAO = daoFactory.getListValueDAO(connection);
        List<ListValue> listValues = new LinkedList<>();
        try {
            listValues.addAll(listValueDAO.getAllListListValue(listId));
        } catch (DAOException e) {
            LOGGER.error(e);
        } finally {
            daoFactory.putConnection(connection);
        }
        return listValues;
    }
}
