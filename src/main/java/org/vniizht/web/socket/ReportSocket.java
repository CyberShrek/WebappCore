package org.vniizht.forge.webapp.web.socket;

import org.vniizht.forge.webapp.util.JSON;
import org.vniizht.forge.webapp.sql.SimpleSet;
import org.vniizht.forge.webapp.sql.SqlService;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

// Used for applying sql-formulas to reports and returning the result in real time, what is impossible via HTTP
@ServerEndpoint("/report")
public class ReportSocket {

    // Cache of frames by session id
    private static final Map<String, Frame> frames = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        frames.put(session.getId(), new Frame());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            Frame frame = frames.get(session.getId());
            updateFrame(frame, JSON.parse(message));
            if((frame.getFormulas() != null || frame.getQuery() != null) && frame.getParams() != null) {
                SimpleSet result = executeFrame(frame);
                session.getBasicRemote().sendText(JSON.stringify(result));
            }
        } catch (Exception e) {
            String error = e.getMessage();
            if(e instanceof SQLException) {
                int cutPositionEn = error.indexOf("\n  Position:");
                if(cutPositionEn > 0)
                    error = error.substring(0, cutPositionEn);
                int cutPositionRu = error.indexOf("\n  Позиция:");
                if(cutPositionRu > 0)
                    error = error.substring(0, cutPositionRu);
            }
            session.getBasicRemote().sendText(error);
        }
    }

    @OnClose
    public void onClose(Session session) {
        frames.remove(session.getId());
    }

    private static void updateFrame(Frame frame, Map<String, Object> json) {
        Optional.ofNullable(json.get("query")).ifPresent(frame::setQuery);
        Optional.ofNullable(json.get("mutationQuery")).ifPresent(frame::setMutationQuery);
        Optional.ofNullable(json.get("columnNames")).ifPresent(frame::setColumnNames);
        Optional.ofNullable(json.get("mainData")).ifPresent(frame::setMainData);
        Optional.ofNullable(json.get("bunchData")).ifPresent(frame::setBunchData);
        Optional.ofNullable(json.get("params")).ifPresent(frame::setParams);
        Optional.ofNullable(json.get("formulas")).ifPresent(frame::setFormulas);
    }

    private static SimpleSet executeFrame(Frame frame) throws SQLException {

        if (frame.getMutationQuery() != null && frame.getMainData() != null)
            mutateFrame(frame);

        SimpleSet resultSet = frame.getColumnNames() != null && frame.getMainData() != null
                ? new SimpleSet(frame.getColumnNames(), frame.getMainData())
                : null;

        SimpleSet bunchSet = frame.getColumnNames() != null && frame.getBunchData() != null
                ? new SimpleSet(frame.getColumnNames(), frame.getBunchData())
                : null;

        return frame.getFormulas() != null && frame.getParams() != null
                ? SqlService.executeFormulas(frame.getFormulas(), frame.getParams(), resultSet, bunchSet)
                : frame.getQuery() != null && frame.getParams() != null
                ? SqlService.executeQuery(
                        frame.getQuery(), frame.getParams(), new SimpleSet(frame.getColumnNames(), frame.getMainData()))
                : new SimpleSet();
    }

    private static void mutateFrame(Frame frame) throws SQLException {
        SimpleSet mutatedSet = SqlService.executeQuery(
                frame.getMutationQuery(),
                frame.getParams(),
                new SimpleSet(frame.getColumnNames(), frame.getMainData())
        );
        frame.setColumnNames(mutatedSet.getColumnNames());
        frame.setMainData(mutatedSet.getData());
        frame.setMutationQuery(null);
    }
}

class Frame {
    private String query;
    private String mutationQuery;
    private List<String> formulas;
    private List<String> columnNames;
    private List<List<Object>> mainData;
    private List<List<Object>> bunchData;
    private Map<String, Object> params;

    public String getQuery() {
        return query;
    }
    public void setQuery(Object query) {
        this.query = (String) query;
    }

    public String getMutationQuery() {
        return mutationQuery;
    }
    public void setMutationQuery(Object mutationQuery) {
        this.mutationQuery = (String) mutationQuery;
    }

    public List<String> getFormulas() {
        return formulas;
    }
    public void setFormulas(Object formulas) {
        this.formulas = (List<String>) formulas;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }
    public void setColumnNames(Object columnNames) {
        this.columnNames = (List<String>) columnNames;
    }

    public List<List<Object>> getMainData() {
        return mainData;
    }
    public void setMainData(Object matrixData) {
        this.mainData = (List<List<Object>>) matrixData;
    }

    public List<List<Object>> getBunchData() {
        return bunchData;
    }
    public void setBunchData(Object matrixData) {
        this.bunchData = (List<List<Object>>) matrixData;
    }

    public Map<String, Object> getParams() {
        return params;
    }
    public void setParams(Object params) {
        this.params = (Map<String, Object>) params;
    }
}
