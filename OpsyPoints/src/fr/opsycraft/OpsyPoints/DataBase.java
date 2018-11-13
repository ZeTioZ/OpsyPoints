package fr.opsycraft.OpsyPoints;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class DataBase
{
  private String host;
  private String name;
  private String user;
  private String pass;
  private String url;
  private Connection conn;
  
  public DataBase(String h, String n, String u, String p)
  {
    this.host = h;
    this.name = n;
    this.user = u;
    this.pass = p;
    this.url = ("jdbc:mysql://" + this.host + "/" + this.name);
  }
  
  public void connection()
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    try
    {
      this.conn = DriverManager.getConnection(this.url, this.user, this.pass);
      Bukkit.getServer().getLogger().info("[OpsyPoints] Connexion à la BDD réussie.");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      Bukkit.getServer().getLogger().info("[OpsyPoints] Impossible de se connecter à la BDD.");
    }
  }
  
  public boolean connected()
  {
    return this.conn != null;
  }
  
  public void connectIfNot()
  {
    if (!connected()) {
      connection();
    }
  }
  
  public void disconnection()
  {
    if (connected()) {
      this.conn = null;
    }
  }
  
  public String getString(String request, int ci)
  {
    connectIfNot();
    try
    {
      Statement state = this.conn.createStatement();
      ResultSet result = state.executeQuery(request);
      try
      {
        if (result.next()) {
          return result.getString(ci);
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      return "NULL";
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return request;
  }
  
  public int getInt(String request, int ci)
  {
    connectIfNot();
    try
    {
      Statement state = this.conn.createStatement();
      ResultSet result = state.executeQuery(request);
      try
      {
        if (result.next()) {
          return result.getInt(ci);
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      return -1;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return ci;
  }
  
  public void sendRequest(String request)
  {
    connectIfNot();
    try
    {
      Statement state = this.conn.createStatement();
      state.executeUpdate(request);
      state.close();
      Bukkit.getServer().getLogger().info("[OpsyPoints] L'opération a réussis.");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      Bukkit.getServer().getLogger().info("[OpsyPoints] L'opération a échouée.");
    }
  }
}
