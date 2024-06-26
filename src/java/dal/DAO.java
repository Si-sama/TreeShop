package dal;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import model.*;

public class DAO {

    public static DAO INSTANCE = new DAO();
    private Connection con;

    // Design Pattern (Singleton)
    private DAO() {
        if (INSTANCE == null) {
            con = new DBContext().connect;
        } else {
            INSTANCE = this;
        }
    }

    // Lấy ID với role dafault là 1 (Servlet: Sign Up)
    public int getDefaultRoleId() {
        int defaultRoleId = -1;
        String sql = "SELECT idR FROM Roles WHERE roleName = 'user'";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                defaultRoleId = rs.getInt("idR");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return defaultRoleId;
    }

    // Lấy ra ID sản phẩm theo tên sản phẩm
    public int getIdPbyNameP(String nameP) {
        int idP = -1;
        String sql = "SELECT idP FROM Products WHERE nameP = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nameP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idP = rs.getInt("idP");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idP;
    }

    // Lấy ra Description sản phẩm theo tên sản phẩm
//    public String getDescriptionbyNameP(String nameP) {
//        String DescriptionP = "";
//        String sql = "SELECT descriptionP FROM Products WHERE nameP = ?";
//        try {
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setString(1, nameP);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                DescriptionP = rs.getString("descriptionP");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return DescriptionP;
//    }
    // Kiểm tra tài khoản (Servlet: Login)
    public Accounts getAccount(String name, String password) {
        Accounts account = null;
        String sql = "SELECT * FROM Accounts WHERE nameA = ? AND passwordA = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                account = new Accounts();
                account.setIdA(rs.getInt("idA"));
                account.setNameA(rs.getString("nameA"));
                account.setPasswordA(rs.getString("passwordA"));
                account.setEmail(rs.getString("emailA"));
                Roles roles = new Roles();
                roles.setIdR(rs.getInt("idR"));
                account.setIdR(roles);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    // Kiểm tra email của khách hàng khi khách hàng đăng ký tài khoản (Servlet: Sign up)
    public boolean emailExists(String email) {
        boolean existEmailA = false;
        String sql = "SELECT COUNT(*) AS countMail FROM Accounts WHERE emailA = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("countMail");
                if (count > 0) {
                    existEmailA = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existEmailA;
    }

    // Kiểm tra tên tài khoản của khách hàng khi khách hàng đăng ký tài khoản (Servlet: Sign up)
    public boolean nameAExists(String nameA) {
        boolean existNameA = false;
        String sql = "SELECT COUNT(*) AS countAccount FROM Accounts WHERE nameA = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nameA);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("countAccount");
                if (count > 0) {
                    existNameA = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existNameA;
    }

    // Kiểm tra sản phẩm đã có chưa (Servlet: AddProductAdmin)
    public Products existAddProductAdmin(String nameP, String image, String descriptionP) {
        Products product = null;
        String sql = "SELECT * FROM Products WHERE nameP = ? OR [image] = ? OR descriptionP = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nameP);
            ps.setString(2, image);
            ps.setString(3, descriptionP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new Products();
                product.setIdP(rs.getInt("idP"));
                product.setNameP(rs.getString("nameP"));
                product.setQuantityP(rs.getInt("quantityP"));
                product.setPriceP(rs.getInt("priceP"));
                product.setDescriptionP(rs.getString("descriptionP"));
                product.setImage(rs.getString("image"));
                Categories c = getCategoryById(rs.getInt("idC"));
                product.setIdC(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    // Kiểm tra sản phẩm đã có chưa (Servlet: UpdateProductAdmin)
    public Products existUpProductAdmin(String nameP, String descriptionP) {
        Products product = null;
        String sql = "SELECT * FROM Products WHERE nameP = ? OR descriptionP = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nameP);
            ps.setString(2, descriptionP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new Products();
                product.setIdP(rs.getInt("idP"));
                product.setNameP(rs.getString("nameP"));
                product.setQuantityP(rs.getInt("quantityP"));
                product.setPriceP(rs.getInt("priceP"));
                product.setDescriptionP(rs.getString("descriptionP"));
                product.setImage(rs.getString("image"));
                Categories c = getCategoryById(rs.getInt("idC"));
                product.setIdC(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    // Thêm tài khoản mới (Servlet: Sign Up)
    public void insertAccount(Accounts account) {
        String sql = "INSERT INTO [dbo].[Accounts] ([emailA], [nameA], [passwordA], [idR]) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, account.getEmail());
            ps.setString(2, account.getNameA());
            ps.setString(3, account.getPasswordA());
            ps.setInt(4, account.getIdR().getIdR());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy dữ liệu từ bảng Product (Servlet: AddProductAdmin, ProductManagement,  UpdateProductAdmin)
    public List<Products> getAllProducts() {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setIdP(rs.getInt("idP"));
                p.setNameP(rs.getString("nameP"));
                p.setQuantityP(rs.getInt("quantityP"));
                p.setPriceP(rs.getInt("priceP"));
                p.setDescriptionP(rs.getString("descriptionP"));
                p.setImage(rs.getString("image"));
                Categories c = getCategoryById(rs.getInt("idC"));
                p.setIdC(c);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm này để sử dụng khi quantity <= 0 thì hết hàng và không hiển thị phía khách hàng 
//    (test case với trường hợp admin cập nhật hết hàng) - đúng quy trình sẽ là khách hàng mua hàng sẽ trừ đi quantity của Products
//    (Servlet: Category, Default)
    public List<Products> getProducts() {
        List<Products> allProduct = getAllProducts();
        List<Products> outOfStock = new ArrayList<>();
        for (Products p : allProduct) {
            if (p.getQuantityP() > 0) {
                outOfStock.add(p);
            }
        }
        return outOfStock;
    }

//    Lấy dữ liệu từ bảng Product khi quantity > 0
//    public List<Products> checkProductsQuantity() {
//        List<Products> list = new ArrayList<>();
//        String sql = "SELECT * FROM Products WHERE quantityP > 0";
//        try {
//            PreparedStatement st = con.prepareStatement(sql);
//            ResultSet rs = st.executeQuery();
//            while (rs.next()) {
//                Products p = new Products();
//                p.setIdP(rs.getInt("idP"));
//                p.setNameP(rs.getString("nameP"));
//                p.setQuantityP(rs.getInt("quantityP"));
//                p.setPriceP(rs.getInt("priceP"));
//                p.setDescriptionP(rs.getString("descriptionP"));
//                p.setImage(rs.getString("image"));
//                Categories c = getCategoryById(rs.getInt("idC"));
//                p.setIdC(c);
//                list.add(p);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
    // Lấy dữ liệu từ bảng Category (Servlet: AddProductAdmin, Category, Contact, Default, New, Product, Search, Show, SubCategory, UpdateProductAdmin)
    public List<Categories> getCategory() {
        List<Categories> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Categories c = new Categories();
                c.setIdC(rs.getInt("idC"));
                c.setNameC(rs.getString("nameC"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy dữ liệu từ bảng Category theo id (có dùng tận 7 hàm trong DAO - Chưa dùng ngoài)
    public Categories getCategoryById(int idC) {
        String sql = "SELECT * FROM Categories WHERE idC = ?";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, idC);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Categories c = new Categories();
                c.setIdC(rs.getInt("idC"));
                c.setNameC(rs.getString("nameC"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy dữ liệu từ bảng Products theo idP (Servlet: UpdateProductAdmin)
    public List<Products> getProductsByPid(int idP) {
        String sql = "SELECT p.*, c.* FROM Products p JOIN Categories c ON p.idC = c.idC WHERE p.idP = ?";
        List<Products> list = new ArrayList<>();
        try {
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, idP);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setIdP(rs.getInt("idP"));
                p.setNameP(rs.getString("nameP"));
                p.setQuantityP(rs.getInt("quantityP"));
                p.setPriceP(rs.getInt("priceP"));
                p.setDescriptionP(rs.getString("descriptionP"));
                p.setImage(rs.getString("image"));
                Categories c = getCategoryById(rs.getInt("idC"));
                p.setIdC(c);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy sản phẩm theo ID danh mục (Servlet: SubCategory)
    public List<Products> getProductsByCategoryId(int cid) {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE idC = ?";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, cid);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setIdP(rs.getInt("idP"));
                p.setNameP(rs.getString("nameP"));
                p.setQuantityP(rs.getInt("quantityP"));
                p.setPriceP(rs.getInt("priceP"));
                p.setDescriptionP(rs.getString("descriptionP"));
                p.setImage(rs.getString("image"));

                Categories c = getCategoryById(rs.getInt("idC"));
                p.setIdC(c);

                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm kiếm sản phẩm theo tên (Servlet: Search)
    public List<Products> search(String name) {
        List<Products> productSearch = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE 1 = 1 ";
        if (name != null && !name.isEmpty()) {
            sql += "AND nameP LIKE ?";
        }
        try {
            PreparedStatement st = con.prepareStatement(sql);
//            st.setString(1, "%" + name + "%");
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setIdP(rs.getInt("idP"));
                p.setNameP(rs.getString("nameP"));
                p.setQuantityP(rs.getInt("quantityP"));
                p.setPriceP(rs.getInt("priceP"));
                p.setDescriptionP(rs.getString("descriptionP"));
                p.setImage(rs.getString("image"));
                Categories c = getCategoryById(rs.getInt("idC"));
                p.setIdC(c);
                productSearch.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productSearch;
    }

    // Lấy chi tiết sản phẩm theo ID (Servlet: DetailProduct)
    public List<Products> getProductsByID(int idP) {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE idP = ?";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, idP);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setIdP(rs.getInt("idP"));
                p.setNameP(rs.getString("nameP"));
                p.setQuantityP(rs.getInt("quantityP"));
                p.setPriceP(rs.getInt("priceP"));
                p.setDescriptionP(rs.getString("descriptionP"));
                p.setImage(rs.getString("image"));

                Categories c = getCategoryById(rs.getInt("idC"));
                p.setIdC(c);

                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy sản phẩm theo danh mục với thứ tự ngẫu nhiên (Servlet: DetailProduct)
    public List<Products> getRandomProductsForCategory(int idC) {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT TOP 4 p.*, c.* FROM Products p JOIN Categories c ON p.idC = c.idC WHERE c.idC = ? ORDER BY NEWID()";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, idC);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setIdP(rs.getInt("idP"));
                p.setNameP(rs.getString("nameP"));
                p.setQuantityP(rs.getInt("quantityP"));
                p.setPriceP(rs.getInt("priceP"));
                p.setDescriptionP(rs.getString("descriptionP"));
                p.setImage(rs.getString("image"));
                Categories c = new Categories();
                c.setIdC(rs.getInt("idC"));
                c.setNameC(rs.getString("nameC"));
                p.setIdC(c);

                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm data liên hệ khách hàng (Servlet: Contact)
    public void insertContact(Contact c) {
        String sql = "INSERT INTO [dbo].[Contacts] "
                + "([fullNameCO], [emailAddressCO], [phoneNumberCO], [messageCO]) "
                + "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getFullNameCO());
            ps.setString(2, c.getEmailAddressCO());
            ps.setInt(3, c.getPhoneNumberCO());
            ps.setString(4, c.getMessageCO());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thêm data sản phẩm (Servlet: AddProductAdmin)
    public void insertProduct(Products pAdd) {
        String sql = "INSERT INTO [dbo].[Products]\n"
                + "           ([nameP]\n"
                + "           ,[quantityP]\n"
                + "           ,[priceP]\n"
                + "           ,[descriptionP]\n"
                + "           ,[image]\n"
                + "           ,[idC])\n"
                + "     VALUES\n"
                + "           (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pAdd.getNameP());
            ps.setInt(2, pAdd.getQuantityP());
            ps.setDouble(3, pAdd.getPriceP());
            ps.setString(4, pAdd.getDescriptionP());
            ps.setString(5, pAdd.getImage());
            ps.setInt(6, pAdd.getIdC().getIdC());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật data sản phẩm (Servlet: UpdateProductAdmin)
    public void updateProducts(Products pUp) {
        String sql = "UPDATE [dbo].[Products]\n"
                + "   SET [nameP] = ?\n"
                + "      ,[quantityP] = ?\n"
                + "      ,[priceP] = ?\n"
                + "      ,[descriptionP] = ?\n"
                + "      ,[image] = ?\n"
                + "      ,[idC] = ?\n"
                + " WHERE [idP] = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pUp.getNameP());
            ps.setInt(2, pUp.getQuantityP());
            ps.setInt(3, pUp.getPriceP());
            ps.setString(4, pUp.getDescriptionP());
            ps.setString(5, pUp.getImage());
            ps.setInt(6, pUp.getIdC().getIdC());
            ps.setInt(7, pUp.getIdP());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa data sản phẩm (Servlet: DeleteProductAdmin)
    public void deleteProduct(int idP) {
        String sql = "DELETE FROM [dbo].[Products]\n"
                + "      WHERE [idP] = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idP);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy liên hệ hiển thị cho admin (Servlet: ContactAdmin)
    public List<Contact> getAllContact() {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM Contacts";
        try {
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Contact co = new Contact();
                co.setIdCO(rs.getInt("idCO"));
                co.setFullNameCO(rs.getString("fullNameCO"));
                co.setEmailAddressCO(rs.getString("emailAddressCO"));
                co.setPhoneNumberCO(rs.getInt("phoneNumberCO"));
                co.setMessageCO(rs.getString("messageCO"));
                list.add(co);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xóa data liên hệ (Servlet: DeleteContactAdmin)
    public void deleteContact(int idCO) {
        String sql = "DELETE FROM [dbo].[Contacts]\n"
                + "      WHERE idCO = ?";
        try ( PreparedStatement ps = con.prepareStatement(sql);){
           
            ps.setInt(1, idCO);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DAO dao = INSTANCE;
//        int id = 0;
//        id = dao.getLastIdP();
//        if (id != 0) {
//            System.out.println("Lấy đc id " + id);
//        } else {
//            System.out.println("chưa lấy được");
//        }
    }
}
