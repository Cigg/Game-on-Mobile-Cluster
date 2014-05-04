% image = imagepath
% split = number of equaly sized pieces on the x-axis
function[] = splitImage(image, split)

% Read the big picture (huehue). Change transparent color to white
% because default in matlab is black and we don't want that.
bigImage = imread(image, 'BackgroundColor',[1 1 1]);

% Get all folder paths and names
currentFolder = cd;
[pathstr,name,ext] = fileparts(image);
folderName = sprintf('%s_split', name);

% create a new dirctory with the same name as the image + '_split'
mkdir(cd, folderName);

X = length(bigImage(:,1,1));
Y = length(bigImage(1,:,1));

% Create how big each chunck should be from input
size = floor(X/split);

for i = 1:size:(X - 1)
    for j = 1:size:(Y - 1)
        endPosX = i+size;
        endPosY = j + size;
        
        if(i == X)
            break;
        end
        
        if(j == Y)
            break;
        end
        
        if(i+size > X)
            endPosX = X;
        end
       
        if(j+size > Y) 
            endPosY = Y;
        end
        image = bigImage(i:endPosX,j:endPosY,:);
        filename = sprintf('%s\\%s_split\\Coordinates_%d_%d.png', cd, name, floor((i - 1)/size), floor((j - 1)/size));
        
        % Create a new image
        imwrite(image, filename);
    end
end
