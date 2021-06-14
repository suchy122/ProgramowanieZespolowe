package Controller.Home;

import Config.DbConnect;
import Config.Pojos.ProductsEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The type Power.
 */
public class Power implements Initializable {

    @FXML
    private TableView<ProductsEntity> powerTable;
    @FXML
    private TableColumn<ProductsEntity, String> powerName;
    @FXML
    private TableColumn<ProductsEntity, Integer> powerVolume;
    @FXML
    private TableColumn<ProductsEntity, String> powerInfo;
    @FXML
    private TableColumn<ProductsEntity, Integer> powerPrice;
    @FXML
    private TableColumn<ProductsEntity, Blob> powerImg;
    @FXML
    private TableColumn powerAddCart;

    private ObservableList<ProductsEntity> powerList;
    private Connection connection;
    /**
     * The Db connect.
     */
    DbConnect dbConnect;

    private static int i = 0;
    /**
     * The Input stream.
     */
    static InputStream inputStream;
    /**
     * The Blob.
     */
    static Blob blob;
    /**
     * The Bool.
     */
    static boolean bool = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbConnect = new DbConnect();
        powerTableView();
        i = 0;
        bool = false;
    }
    /**
     * Method for display powers from DB in a table.
     */
    private void powerTableView() {

        try {
            powerList = FXCollections.observableArrayList();
            String query = "SELECT * FROM products WHERE Category='Zasilacz' AND Archive_status = 0";
            connection = dbConnect.getConnection();
            ResultSet rs = connection.createStatement().executeQuery(query);
            while (rs.next()) {
                ProductsEntity p = new ProductsEntity();
                p.setID_product(rs.getInt("ID_product"));
                p.setName(rs.getString("Name"));
                p.setPrice(rs.getDouble("Price"));
                p.setQuantity(rs.getInt("Quantity"));
                p.setDescription(rs.getString("Description"));
                p.setCategory(rs.getString("Category"));
                p.setImage(rs.getBlob("Image"));
                powerList.add(p);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        powerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        powerVolume.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        powerInfo.setCellValueFactory(new PropertyValueFactory<>("description"));
        powerPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        powerImg.setCellValueFactory(new PropertyValueFactory<>("image"));

        powerTable.setItems(powerList);

        addButtonToTable();


        powerImg.setCellFactory(e -> {
            final ImageView imageView = new ImageView();
            imageView.setFitHeight(powerImg.getMinWidth());
            imageView.setFitWidth(powerImg.getMinWidth());

            TableCell<ProductsEntity, Blob> powerCell = new TableCell<ProductsEntity, Blob>() {
                public void updateItem(Blob image, boolean empty) {
                    try {
                        if (image != null) {
                            inputStream = image.getBinaryStream();
                            imageView.setImage(new Image(inputStream));
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            };
            powerCell.setGraphic(imageView);
            return powerCell;
        });
    }
    /**
     * Method for adding functional buttons to the table.
     */
    private void addButtonToTable() {

        Callback<TableColumn<ProductsEntity, Void>, TableCell<ProductsEntity, Void>> cellFactory = new Callback<TableColumn<ProductsEntity, Void>, TableCell<ProductsEntity, Void>>() {
            @Override
            public TableCell<ProductsEntity, Void> call(final TableColumn<ProductsEntity, Void> param) {
                final TableCell<ProductsEntity, Void> cell = new TableCell<ProductsEntity, Void>() {

                    private final Button btn = new Button();

                    {
                        btn.setId("addCart");
                        btn.setOnAction((ActionEvent event) -> {
                            try {
                                Stage stage = new Stage();
                                AnchorPane ap = FXMLLoader.load(getClass().getResource("/FXML/Login/login.fxml"));
                                Scene scene = new Scene(ap);
                                stage.setScene(scene);
                                stage.setTitle("Logowanie");
                                stage.show();
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        powerAddCart.setCellFactory(cellFactory);
    }
}
